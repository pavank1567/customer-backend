create TABLE otp_details(
    email TEXT not null primary key ,
    otp TEXT not null,
    insert_ts TEXT not null,
    exp_ts TEXT not null
)