create table categorias (
                            id bigint not null auto_increment,
                            descripcion varchar(100) not null,
                            nombre varchar(100) not null,
                            primary key (id)
) engine=InnoDB;

create table productos (
                           precio integer not null check ((precio>=0)),
                           stock_actual integer not null check ((stock_actual>=0)),
                           stock_minimo integer not null check ((stock_minimo>=0)),
                           categoria_id bigint not null,
                           id bigint not null auto_increment,
                           nombre varchar(50) not null,
                           codigo_barras varchar(100) not null,
                           primary key (id)
) engine=InnoDB;

alter table productos
    add constraint FK2fwq10nwymfv7fumctxt9vpgb
        foreign key (categoria_id)
            references categorias (id)