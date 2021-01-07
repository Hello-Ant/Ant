// IBookManager.aidl
package com.lee.ant;

import com.lee.ant.Book;
import com.lee.ant.NewBookObserver;

// Declare any non-default types here with import statements

interface IBookManager {

    oneway void addBook(in Book book);

    oneway void removeBookById(long id);

    List<Book> getAllBook();

    oneway void addNewBookObserver(NewBookObserver observer);

    oneway void removeNewBookObserver(NewBookObserver observer);
}