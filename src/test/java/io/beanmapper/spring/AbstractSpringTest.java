package io.beanmapper.spring;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Base for Spring dependent tests.
 *
 * @author Jeroen van Schagen
 * @since Aug 24, 2015
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { ApplicationConfig.class })
public abstract class AbstractSpringTest {
    
    private JdbcTemplate jdbcTemplate;
    
    @Before
    @After
    public void cleanUp() {
        jdbcTemplate.execute("TRUNCATE SCHEMA public AND COMMIT");
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

}
