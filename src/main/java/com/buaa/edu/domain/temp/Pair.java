package com.buaa.edu.domain.temp;

public class Pair<K, V> {
    private K key;

    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Pair) {
            if (key.equals(((Pair<?, ?>) o).getKey()) && (value.equals(((Pair<?, ?>) o).getValue()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return key.hashCode() * 13 + (value == null ? 0 : value.hashCode());
    }

    public boolean equalsWithoutOrder(Pair<?, ?> pair) {
        if (key.equals(pair.getKey()) && value.equals(pair.getValue())) {
            return true;
        } else {
            if (key.getClass().equals(pair.getValue().getClass())
                    && value.getClass().equals(pair.getKey().getClass())) {
                if (key.equals(pair.getValue()) && value.equals(pair.getKey())) {
                    return true;
                }
            }
        }

        return false;
    }
}
