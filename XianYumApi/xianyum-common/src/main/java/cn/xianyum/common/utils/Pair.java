package cn.xianyum.common.utils;

import java.util.Objects;

/**
 * @author wei.zhang@onecontract-cloud.com
 * @description
 * @date 2022/5/10 14:11
 */
public class Pair<S, T> {

    private S first;
    private T second;

    public Pair() {
    }

    public Pair(S first, T second) {
        this.first = first;
        this.second = second;
    }

    public static <S, T> Pair<S, T> of(S first, T second) {
        return new Pair(first, second);
    }

    public S getFirst() {
        return this.first;
    }

    public void setFirst(S first) {
        this.first = first;
    }

    public T getSecond() {
        return this.second;
    }

    public void setSecond(T second) {
        this.second = second;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Pair)) {
            return false;
        } else {
            Pair<?, ?> pair = (Pair)o;
            return Objects.equals(this.first, pair.first) && Objects.equals(this.second, pair.second);
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.first, this.second});
    }

    public String toString() {
        return "Pair{first=" + this.first + ", second=" + this.second + '}';
    }
}
