package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@Profile("postgres")
public class JdbcMealRepositoryPostgres extends JdbcMealRepository<LocalDateTime> {

    @Autowired
    public JdbcMealRepositoryPostgres(JdbcTemplate jdbcTemplate,
                                      NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Override
    protected LocalDateTime toDbDateTime(LocalDateTime localDateTime) {
        return localDateTime;
    }
}
