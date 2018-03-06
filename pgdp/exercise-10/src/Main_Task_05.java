public class Main_Task_05 {
    static class A {
        void m(A a) {
            System.out.println("A.A+");
        }

        void m(B b) {
            System.out.println("A.B+");
        }

        void m(C c) {
            System.out.println("A.C+");
        }
    }

    static class B extends A {
        void m(A a) {
            System.out.println("B.A+");
        }

        void m(B b) {
            System.out.println("B.B+");
        }

        void m(C c) {
            System.out.println("B.C+");
        }
    }
    static class C extends B { }


    public static void main(String... args) {
        A a = new A();
        B b = new B();
        C c = new C();

        //a.m((A) b);     // A.A+
        //a.m((B) c);     // A.B+
        //a.m(c);         // A.C+
        //b.m(a);         // B.A+
        //b.m((A) b);     // B.A+
        //b.m((A) c);     // B.A+
        //b.m((B) a);     // runtime exception bc while we can cast to a derived type in theory, the casted object simply isn't of that type
        //b.m(c);         // B.C+
    }
}
