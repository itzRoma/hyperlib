package my.projects.hyperlib.service.implementation;

import my.projects.hyperlib.entity.Role;
import my.projects.hyperlib.repository.RoleRepository;
import my.projects.hyperlib.service.CommonServiceContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService implements CommonServiceContract<Role> {
    private RoleRepository roleRepository;

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public void save(Role entity) {
        roleRepository.save(entity);
    }

    @Override
    public void delete(Role entity) {
        roleRepository.save(entity);
    }

    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }
}
