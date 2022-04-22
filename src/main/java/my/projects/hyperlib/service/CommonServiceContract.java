package my.projects.hyperlib.service;

import java.util.List;

public interface CommonServiceContract<T> {
    List<T> findAll();
    void save(T entity);
    void delete(T entity);
    void update(T source, T target);
}
