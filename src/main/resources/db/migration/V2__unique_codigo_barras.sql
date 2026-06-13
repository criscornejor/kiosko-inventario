alter table productos
    add constraint uk_productos_codigo_barras unique (codigo_barras);
