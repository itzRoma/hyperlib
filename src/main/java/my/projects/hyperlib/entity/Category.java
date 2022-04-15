package my.projects.hyperlib.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotEmpty(message = "Name cannot be empty!")
    private String name;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ItemType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "categories_item_types", joinColumns = @JoinColumn(name = "category_id"))
    private Set<ItemType> itemTypes = new HashSet<>();
}
