package com.stalwart.customer.mapper;

import com.stalwart.customer.model.Customer;
import com.stalwart.customer.model.Gender;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("name");
        when(resultSet.getString("email")).thenReturn("email");
        when(resultSet.getInt("age")).thenReturn(22);
        when(resultSet.getString("gender")).thenReturn("MALE");
        when(resultSet.getString("password")).thenReturn("password");

        Customer actual = customerRowMapper.mapRow(resultSet,1);

        Customer expected = new Customer(
                1,
                "name",
                "email",
                "password",
                22,
                Gender.MALE );
        assertThat(actual).isEqualTo(expected);
    }
}