package com.tabbeo.detectorAnalyser.Analyser;


import java.util.Iterator;

public class RangeUtils {
    public static abstract class NamedParamRange<T extends Number> implements Iterable<T>{
        Enum _name;
        T[] _elements;

        public NamedParamRange(Enum name, T minValue, T maxValue, T step) {
            _name = name;
            int nElements = areEqual(minValue,maxValue)? 1 : calculateNElements(minValue, maxValue, step);
            _elements = allocArray(nElements);

            T val = minValue;
            for (int i = 0; i < nElements; ++i) {
                _elements[i] = val;
                val = sum(val, step);
            }
        }

        final public Enum getName(){ return _name; }
        final public T[] getElements(){ return _elements; }

        final public T getAt(int pos){
            return _elements[pos];
        }

        final public int size(){ return _elements.length; }

        final private int calculateNElements(T min, T max, T step) {
            return (int) Math.ceil(div((substract(sum(max, step), min)), step)); // Max included
        }

        protected abstract T sum(T l, T r);

        protected abstract T substract(T l, T r);

        protected abstract double div(T l, T r);

        protected abstract T[] allocArray(int nElements);

        protected abstract boolean areEqual(T l, T r);

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                int count=0;
                @Override
                public boolean hasNext() {
                    return count<_elements.length;
                }

                @Override
                public T next() {
                    count++;
                    return _elements[count-1];
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

    public static class IntNamedParamRange extends NamedParamRange<Integer> {

        public IntNamedParamRange(Enum name, Integer minValue, Integer maxValue, Integer step) {
            super(name,minValue, maxValue, step);
        }

        @Override
        protected Integer sum(Integer l, Integer r) {
            return l + r;
        }

        @Override
        protected Integer substract(Integer l, Integer r) {
            return l - r;
        }

        @Override
        protected double div(Integer l, Integer r) {
            return l / (double) r;
        }

        @Override
        protected Integer[] allocArray(int nElements) {
            return new Integer[nElements];
        }

        @Override
        protected boolean areEqual(Integer l, Integer r) {
            return l.equals(r);
        }
    }

    public static class DoubleNamedParamRange extends NamedParamRange<Double> {

        public DoubleNamedParamRange(Enum name, Double minValue, Double maxValue, Double step) {
            super(name, minValue, maxValue, step);
        }

        @Override
        protected Double sum(Double l, Double r) {
            return l + r;
        }

        @Override
        protected Double substract(Double l, Double r) {
            return l - r;
        }

        @Override
        protected double div(Double l, Double r) {
            return l / r;
        }

        @Override
        protected Double[] allocArray(int nElements) {
            return new Double[nElements];
        }

        @Override
        protected boolean areEqual(Double l, Double r) {
            return l.equals(r);
        }
    }
}
