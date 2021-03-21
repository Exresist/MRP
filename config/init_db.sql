CREATE TABLE product
(
    uuid varchar NOT NULL PRIMARY KEY CONSTRAINT product_pk,
    full_name text NOT NULL
);

CREATE TABLE materials
(
    id SERIAL NOT NULL,
    uuid varchar NOT NULL REFERENCES product,
    name text NOT NULL,
    quantity INT NOT NULL
);