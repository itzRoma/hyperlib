package com.itzroma.hyperlib.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;
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
    private LocalDate addingDate;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id) && Objects.equals(code, item.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }
}
