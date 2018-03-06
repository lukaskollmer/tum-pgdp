# task-05

| #   | Statement     | Result     |
| :--- | :------------ | :--------- |
| 1  | `a.m((A) b);` | A.A+       |
| 2  | `a.m((B) c);` | A.B+       |
| 3  | `a.m(c);`     | A.C+       |
| 4  | `b.m(a);`     | B.A+       |
| 5  | `b.m((A) b);` | B.A+       |
| 6  | `b.m((A) c);` | B.A+       |
| 7  | `b.m((B) a);` | runtime exception (see below) |
| 8  | `b.m(c);`     | B.C+       |



**Why does #7 cause the runtime to throw an exception?**  
While we can in theory cast to a derived type, the casted object in this specific example simply isn't of the type we're casting it to
