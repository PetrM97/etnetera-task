package com.etnetera.hr.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.sql.Date;

/**
 * Simple data entity describing basic properties of every JavaScript framework.
 *
 * @author Etnetera
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "javascriptframework")
public class JavaScriptFramework {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 30)
    @NotEmpty(message = "NotEmpty")
    @Size(max = 30)
    private String name;

    @Column(nullable = true, length = 30)
    @Size(max = 30)
    // Regex for semantic versioning,
    // taken from: https://semver.org/#is-there-a-suggested-regular-expression-regex-to-check-a-semver-string
    @Pattern(regexp = "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$")
    private String version;

    @Column(nullable = true, name = "deprecation_date")
    private Date deprecationDate;

    @Column(nullable = true, name = "hype_level")
    @Range(min = 0, max = 10)
    private int hypeLevel;

    public JavaScriptFramework() {
    }

    public JavaScriptFramework(String name) {
        this.name = name;
    }

}
