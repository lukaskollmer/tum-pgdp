# task-06


Gegeben:
```sh
letter := a|...|z
bbisz  := b|...|z
cbisz  := c|...|z
```

**Es kommen mindestens zwei, maximal vier _a_’s im Text vor**

```sh
2_times := bbisz* a bbisz* a bbisz*
3_times := bbisz* a bbisz* a bbisz* a bbisz*
4_times := bbisz* a bbisz* a bbisz* a bbisz* a bbisz*

solution := 2_times | 3_times | 4_times
```

**Alle _a_’s stehen an geraden Positionen**

```sh
solution := (bbisz a)*
```

**Vor jedem _a_ ein einzelnes _b_**

```sh
solution := (cbisz* b cbisz* a)*
```
