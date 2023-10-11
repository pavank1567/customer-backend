package com.stalwart.customer.mapper;

import com.stalwart.customer.model.Customer;
import com.stalwart.customer.model.Gender;
import com.stalwart.customer.model.OTPObject;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class OTPRowMapper implements RowMapper {
    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new OTPObject(
                rs.getString("email"),
                rs.getString("otp"),
                rs.getLong("insert_ts"),
                rs.getLong("exp_ts")
        );
    }
}
