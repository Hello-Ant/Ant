// NewBookObserver.aidl
package com.lee.ant;

import com.lee.ant.Book;

// Declare any non-default types here with import statements

interface NewBookObserver {
    void onNewBookArrived(in Book book);
}