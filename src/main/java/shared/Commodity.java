package shared;

public class Commodity {
    public class Size {
        private int length;
        private int width;
        private int height;

        public Size(int length, int width, int height) {
            this.length = length;
            this.width = width;
            this.height = height;
        }
    }

    private int weight;
    private Size size;

    public Commodity(int weight, int length, int width, int height) {
        this.weight = weight;
        this.size = new Size(length, width, height);
    }
}
