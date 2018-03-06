# task_07

## Usage

- `task_07_test.java` contains a bunch of tests
- you can run them simply by running `task_07_test.main` (no need for junit)
- your cwd has to be called `src` and `task_07` should be a subdirectory

(unix)
1. Compile everything (Since we load some classes dynamically, we need to make sure that they are in our `CLASSPATH`)
```bash
find ./task_07 -name "*.java" -print | xargs javac
```

2. Run the tests
```bash
java task_07/task_07_test
```


cleanup:

```bash
find ./task_07 -name "*.class" -type f -delete
```

# task_08

(same as above, but the tests are in `task_07_test.java`)
