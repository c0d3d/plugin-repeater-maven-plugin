void assertFileWith(String path, String contents) {
    File expectedFile = new File( basedir, "target/$path" )
    assert expectedFile.isFile()
    assert expectedFile.text == contents
}

assertFileWith("1", "first file contents!")
assertFileWith("2", "second file contents!")
assertFileWith("3", "third file contents!")
