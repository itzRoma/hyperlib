package my.projects.hyperlib.util;

import my.projects.hyperlib.entity.Role;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, String> {
    @Override
    public String convertToDatabaseColumn(Role role) {
        if (role == null)
            return null;

        return role.getName();
    }

    @Override
    public Role convertToEntityAttribute(String name) {
        if (name == null)
            return null;

        return Stream.of(Role.values())
                .filter(role -> role.getName().equals(name))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
