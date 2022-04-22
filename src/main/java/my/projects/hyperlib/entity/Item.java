package my.projects.hyperlib.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Size(min = 6, max = 6, message = "Provide 6-symbols code!")
    private String code;

    @NotBlank(message = "Title cannot be empty!")
    private String title;

    @NotBlank(message = "Author cannot be empty!")
    private String author;

    @Size(max = 1000, message = "Description is too big!")
    private String description;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date addingDate;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "items_categories",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories;

    public Item(String code, String title, String author, String description, ItemType itemType, Set<Category> categories) {
        this.code = code;
        this.title = title;
        this.author = author;
        this.description = description;
        this.itemType = itemType;
        this.categories = categories;
    }

    @PrePersist
    public void prePersist() {
        imageUrl = "/img/common/defaultItemImage.png";
    }
}
