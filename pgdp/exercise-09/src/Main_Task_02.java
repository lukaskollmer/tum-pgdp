import sun.jvm.hotspot.opto.Block_Array;

public class Main_Task_02 {
    public static void main(String... args) {

        NEck _8eck = new NEck(6, 10);

        System.out.format("8eck: umfang=%s area=%s\n", _8eck.umfang(), _8eck.area());

        Prism prism = new Prism(4, 4, 4);
        System.out.format("prism: volume=%s surface=%s\n", prism.volume(), prism.surfaceArea());


        Area area = new Area(2, 2);
        //Square square = area.toSquare();
        System.out.format("area: area=%s umfang=%s\n", area.area(), area.umfang());
        //System.out.format("square: area=%s umfang=%s\n", square.area(), square.umfang());


        System.out.format("%s\n", getArea(_8eck));

    }


    static int getArea(Area someArea) {
        Visitor v = new Visitor();
        someArea.accept(v);
        return v._area;
    }


    static class Area {
        int width;
        int height;

        Area(int width, int height) {
            this.width = width;
            this.height = height;
        }

        int umfang() {
            return 2 * width + 2 * height;
        }

        int area() {
            return width * height;
        }


        boolean isSquare() {
            return this.width == this.height;
        }

        Square toSquare() {
            return (Square)this;
        }

        void accept(Visitor visitor) {
            visitor.visit(this);
        }


        @Override
        public String toString() {
            return String.format("<Area width=%s height=%s>", width, height);
        }
    }


    static class Circle extends Area {
        int radius;

        Circle(int radius) {
            super(0, 0);
            this.radius = radius;
        }

        @Override
        int umfang() {
            return (int) (2 * Math.PI * radius);
        }

        @Override
        int area() {
            return (int) (Math.pow(radius, 2) * Math.PI);
        }
    }

    static class Rectangle extends Area {
        Rectangle(int width, int height) {
            super(width, height);
        }
    }

    static class Square extends Area {
        Square(int width, int height) {
            super(width, height);
        }
    }


    static class NEck extends Area {
        int numberOfCorners;
        int edgeLength;
        NEck(int numberOfCorners, int edgeLength) {
            super(0, 0);

            this.numberOfCorners = numberOfCorners;
            this.edgeLength = edgeLength;
        }


        @Override
        int umfang() {
            return edgeLength * numberOfCorners;
        }

        @Override
        int area() {
            return (int) ((numberOfCorners * Math.pow(edgeLength, 2) / 4) * 1.0 / Math.tan(Math.PI / numberOfCorners));
        }

        @Override
        boolean isSquare() {
            return numberOfCorners == 4;
        }
    }


    static class Prism extends NEck {

        Prism(int numberOfCorners, int edgeLength, int height) {
            super(numberOfCorners, edgeLength);

            this.height = height;
        }

        int volume() {
            return super.area() * height;
        }


        int surfaceArea() {
            int sideArea = new Area(this.edgeLength, this.height).area();

            return super.area() * 2 + numberOfCorners * sideArea;
        }

        boolean isCube() {
            return this.width == this.height && this.edgeLength == this.height;
        }
    }


    static class Visitor {
        int _area;


        void visit(Area area) {
            _area = area.area();
        }

        void visit(Circle circle) {
            _area = circle.area();
        }

        void visit(Prism prism) {
            _area = prism.area();
        }

        void visit(NEck nEck) {
            _area = nEck.area();
        }
    }
}
