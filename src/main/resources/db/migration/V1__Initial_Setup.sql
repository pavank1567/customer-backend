create TABLE customer(
    id BIGSERIAL primary key ,
    name TEXT not null ,
    email TEXT not null ,
    age int not null,
    password TEXT not null,
    gender TEXT not null
)