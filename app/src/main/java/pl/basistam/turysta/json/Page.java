package pl.basistam.turysta.json;

import java.util.List;

import lombok.Data;

@Data
public class Page<T> {
    private List<T> content;
    private int totalPages;
    private int totalElements;
    private int numberOfElements;
    private int size;
    private int number;
}
