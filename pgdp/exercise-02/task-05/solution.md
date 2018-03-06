# task-05


Gegeben:
```sh
letter  := a|...|z
special := _|.|−
digit   := 0|...|9
```

## Aufgabe 1: Telefonnummer

Grundbausteine:
```sh
lvw := 49 | 37 | 1876...  // Ländervorwahl
ow  := 89 | 99...         // Ortsvorwahl
nr  := digit+             // Anschlussnummer
```

Ergebnis:
```sh
number :=  ((+|00 lvw ow) | 0 ow ) nr
```


## Aufgabe 2: E-Mail Adresse

```sh
name := letter+ | (letter+ special+ letter+)+

adresse := name @ (name . name)+
```
