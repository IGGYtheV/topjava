package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    private final ResultSetExtractor<Collection<User>> rs;


    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.rs = new UserExtractor();
    }

    @Override
    @Transactional
    public User save(User user) {
        var parameterSource = new BeanPropertySqlParameterSource(user);
        Role[] roles = user.getRoles().toArray(new Role[0]);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
            batchSave(user, (Integer) newKey, roles);
        } else if (namedParameterJdbcTemplate.update("""
                   UPDATE users SET name=:name, email=:email, password=:password,
                   registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                """, parameterSource) == 0 || batchUpdate(user, user.id(), roles).length < 1) {
            return null;
        }
        return user;
    }

    private int[] batchSave(User user, Integer id, Role[] roles) {
        return jdbcTemplate.batchUpdate("INSERT INTO user_roles(user_id, role) values(?,?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, id);
                ps.setString(2, String.valueOf(roles[i]));
            }

            @Override
            public int getBatchSize() {
                return user.getRoles().size();
            }
        });
    }

    private int[] batchUpdate(User user, Integer id, Role[] roles) {
        return jdbcTemplate.batchUpdate(
                "INSERT INTO user_roles(user_id, role) values(?,?) ON CONFLICT(user_id, role) DO NOTHING ",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, id);
                        ps.setString(2, String.valueOf(roles[i]));
                    }

                    @Override
                    public int getBatchSize() {
                        return user.getRoles().size();
                    }
                });
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        String sql = "SELECT u.*, r.role AS role FROM users u LEFT OUTER JOIN user_roles r ON u.id = r.user_id WHERE id=?";
        Collection<User> users = jdbcTemplate.query(sql, rs, id);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public User getByEmail(String email) {
        String sql = "SELECT u.*, r.role AS role FROM users u LEFT OUTER JOIN user_roles r ON u.id = r.user_id WHERE email=?";
        Collection<User> users = jdbcTemplate.query(sql, rs, email);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT u.*, r.role AS role FROM users u LEFT OUTER JOIN user_roles r ON u.id = r.user_id ORDER BY name, email";
        Collection<User> usersCol = jdbcTemplate.query(sql, rs);
        return List.copyOf(Objects.requireNonNull(usersCol));
    }
}

class UserExtractor implements ResultSetExtractor<Collection<User>> {

    @Override
    public Collection<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, User> userKeyUser = new LinkedHashMap<>();
        Map<Integer, Set<Role>> roleKeyRole = new LinkedHashMap<>();

        while (rs.next()) {
            var userKey = rs.getInt("ID");
            var user = userKeyUser.get(userKey);
            if (user == null) {
                user = new User();
                userKeyUser.put(userKey, user);
                user.setId(userKey);
                user.setName(rs.getString("NAME"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRegistered(rs.getDate("registered"));
                user.setEnabled(rs.getBoolean("enabled"));
                user.setCaloriesPerDay(rs.getInt("calories_per_day"));
            }
            roleKeyRole.putIfAbsent(userKey, new LinkedHashSet<>());
            String roleString = rs.getString("role");
            if (roleString != null) {
                Role role = Role.valueOf(roleString);
                roleKeyRole.get(userKey).add(role);
            }
            user.setRoles(roleKeyRole.get(userKey));
        }
        return userKeyUser.values();
    }
}
