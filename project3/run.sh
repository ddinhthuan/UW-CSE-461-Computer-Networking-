#!/bin/bash


javac Lab3/ProxyServer.java Lab3/HttpHeader.java Lab3/Forward.java
java Lab3.ProxyServer $1
