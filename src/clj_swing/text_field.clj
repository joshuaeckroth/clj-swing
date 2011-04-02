(ns clj-swing.text-field
  (:use [clj-swing core document])
  (:import (javax.swing JTextField JTextArea ListModel)
	   (javax.swing.event ListDataEvent ListDataListener ListSelectionListener)))

(def *text-field-known-keys* [:action :str-ref])

(defmacro text-field [& {action :action str-ref :str-ref :as opts}]
  `(doto (JTextField.)
     ~@(if action  
	 [`(add-action-listener ~action)])
     ~@(if str-ref  
	 [`(add-str-ref-doc-listener ~str-ref)])
     ~@(auto-setters JTextField *text-field-known-keys* opts)))

(defmacro text-area [& {action :action str-ref :str-ref :as opts}]
  `(doto (JTextArea.)
     ~@(if action
         [`(add-action-listener ~action)])
     ~@(if str-ref
         [`(add-str-ref-doc-listener ~str-ref)])
     ~@(auto-setters JTextArea *text-field-known-keys* opts)))


