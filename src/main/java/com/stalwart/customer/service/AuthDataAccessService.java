package com.stalwart.customer.service;

import com.stalwart.customer.dao.AuthenticationDao;
import com.stalwart.customer.dao.CustomerDao;
import com.stalwart.customer.mapper.OTPRowMapper;
import com.stalwart.customer.model.OTPObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthDataAccessService implements AuthenticationDao {


    public final JdbcTemplate jdbcTemplate;

    public final OTPRowMapper otpRowMapper;

    public AuthDataAccessService(JdbcTemplate jdbcTemplate, OTPRowMapper otpRowMapper, CustomerDao customerDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.otpRowMapper = otpRowMapper;
    }

    @Override
    public void storeOTP(String email, String otp) {

        //delete previously stored otp
        String deleteQuery = "delete from otp_details where email = ?";
        jdbcTemplate.update(deleteQuery, email);

        String storeQuery = "insert into otp_details(email,otp,insert_ts,exp_ts) values(?,?,?,?)";
        long ts = System.currentTimeMillis();
        jdbcTemplate.update(storeQuery, email, otp, ts , ts+30000);
    }

    @Override
    public Optional<OTPObject> validateOTP(String email, String otp, long ts) {
        String selectQuery = "select * from otp_details where email=? and otp=?";
        Optional<OTPObject> otpObject = jdbcTemplate.query(selectQuery,otpRowMapper,email,otp).stream().findFirst();
        return otpObject;
    }
}
