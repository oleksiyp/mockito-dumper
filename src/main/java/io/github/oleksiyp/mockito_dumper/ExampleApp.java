package io.github.oleksiyp.mockito_dumper;

public class ExampleApp {
    private int def;
    private float ghi;
    private double jkl;
    private String mno;

    public static void main(String[] args) {
        ExampleApp abc = new ExampleApp();
        abc.def = 5;
        abc.ghi = 5f;
        abc.jkl = 5d;
        abc.mno = "mno";

        StringBuilder sb = new StringBuilder();
        sb.append("test");
        sb.append("str");
        abc.mno = sb.toString();
    }
}
