package net.lzzy.booksmanager.models;

/**
 * Created by 007 on 2016/6/9.
 * 观察事件
 */
public class ObservationEvent {
    private String bookId;
    private int pos;

    public ObservationEvent(String bookId) {
        this.bookId = bookId;
    }

    public ObservationEvent() {
    }

    public String getBookId() {
        return bookId;
    }

    public ObservationEvent(int pos) {
        this.pos = pos;
    }

    public int getPos() {
        return pos;
    }
}
