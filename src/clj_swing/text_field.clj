(ns clj-swing.text-field
  (:use [clj-swing core document])
  (:import (javax.swing JTextField JTextArea JTextPane ListModel)
	   (javax.swing.event ListDataEvent ListDataListener ListSelectionListener)))

(def *text-field-known-keys* [:action :str-ref])
(def *text-pane-known-keys* [:action :str-ref])

(defmacro text-field [& {action :action str-ref :str-ref :as opts}]
  `(doto (JTextField.)
     ~@(if action  
	 [`(add-action-listener ~action)])
     ~@(if str-ref  
	 [`(add-str-ref-doc-listener ~str-ref)])
     ~@(auto-setters JTextField *text-field-known-keys* opts)))


(def *text-area-known-keys* [:action :str-ref :wrap])

(defmacro text-area [& {action :action str-ref :str-ref wrap :wrap
                        :as opts}]
  `(doto (JTextArea.)
     ~@(if action
         [`(add-action-listener ~action)])
     ~@(if str-ref
         [`(add-str-ref-doc-listener ~str-ref)])
     ~@(if wrap
         [`(.setLineWrap ~wrap)])
     ~@(auto-setters JTextArea *text-area-known-keys* opts)))

(defmacro text-pane-html [& {action :action str-ref :str-ref :as opts}]
  `(doto (JTextPane.)
     (.setEditorKit (javax.swing.text.html.HTMLEditorKit.))
     ~@(if action
         [`(add-action-listener ~action)])
     ~@(if str-ref
         [`(add-str-ref-doc-listener ~str-ref)])
     ~@(auto-setters JTextPane *text-pane-known-keys* opts)))
