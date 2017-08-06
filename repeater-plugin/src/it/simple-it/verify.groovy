void assertFileWith(String path, String contents) {
    File expectedFile = new File( basedir, "target/$path" )
    assert expectedFile.isFile()
    assert expectedFile.text == contents
}

assertFileWith("one", "first file contents!")
assertFileWith("two", "second file contents!")
assertFileWith("three", "third file contents!")
