package pl.basistam.turysta.dto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Converter;

public class Page<T> {
    private List<T> content;
    private int totalPages;
    private int totalElements;
    private int numberOfElements;
    private int size;
    private int number;

    public List<T> getContent() {
        return content;
    }
    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getTotalPages() {
        return totalPages;
    }
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }
    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }
    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }

    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }

    public <S> Page<S> map(Converter<? super T, ? extends S> converter) {
        List<S> resultContent = new ArrayList<>(content.size());
        for (T t: content) {
            try {
                resultContent.add(converter.convert(t));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Page<S> result = new Page<>();
        result.setContent(resultContent);
        result.setNumber(number);
        result.setNumberOfElements(totalElements);
        result.setSize(size);
        result.setTotalElements(totalElements);
        result.setTotalPages(totalPages);
        return result;
    }
}
