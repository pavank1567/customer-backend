package com.stalwart.customer.mapper;

import com.stalwart.customer.model.Customer;
import com.stalwart.customer.model.Gender;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CustomerRowMapper implements RowMapper {
    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Customer(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getInt("age"),
               Gender.valueOf( rs.getString("gender"))
        );
    }
}
