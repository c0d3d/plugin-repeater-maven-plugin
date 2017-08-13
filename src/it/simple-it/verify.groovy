void assertFileWith(String path, String contents) {
    File expectedFile = new File( basedir, "target/$path" )
    assert expectedFile.isFile()
    assert expectedFile.text == contents
}

assertFileWith("1", "first file contents!")
assertFileWith("2", "second file contents!")
assertFileWith("3", "third file contents!")
assertFileWith("default-test-1", "default string")
assertFileWith("default-test-2", "override string")
assertFileWith("conditional-test-2", "This should be in the file!")
assertFileWith("conditional-test-3", "This should be in the other-file!")
assertFileWith("compound-conditional-test-4", "This should be in the file!")
assertFileWith("default-conditional-test-1", "This should *not* be written to the log!")
assertFileWith("default-conditional-test-2", "This should be in the file!")
