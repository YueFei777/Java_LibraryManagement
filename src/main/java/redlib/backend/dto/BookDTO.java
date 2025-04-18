package redlib.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class BookDTO {
    @NotBlank
    @Size(min=10, max=13, message="ISBN长度必须为10-13位")
    private String isbn;

    @NotBlank
    @Size(max=50)
    private String title;

    private String author;

    @Size(max=255)
    private String publisher;

    private LocalDate addedAt;
    private LocalDate updatedAt;

    @PositiveOrZero
    @Builder.Default
    private Integer totalCopiesInStock = 1;

    private String updatedBy; //access from the request header
    private String coverUrl;

    @PositiveOrZero
    private Integer borrowedNumbers;

}
