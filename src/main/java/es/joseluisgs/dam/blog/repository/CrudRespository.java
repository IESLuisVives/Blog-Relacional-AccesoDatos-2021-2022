package es.joseluisgs.dam.blog.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CrudRespository<T, ID> {

    // Operaciones CRUD

    // Obtiene todos
    List<T> findAll();

    // Obtiene por ID
    T getById(ID id);

    // Salva
    T save(T t);

    // Actualiza
    T update(T t);

    // Elimina
    T delete(T t);


}