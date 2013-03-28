(ns clj-swing.core
  (:import (java.awt.event ActionListener)
	   (javax.swing ImageIcon SwingUtilities)))

;; taken from clojure.contrib.swing-utils
(defn do-swing*
  "Runs thunk in the Swing event thread according to schedule:
    - :later => schedule the execution and return immediately
    - :now   => wait until the execution completes."
  [schedule thunk]
  (cond
   (= schedule :later) (SwingUtilities/invokeLater thunk)
   (= schedule :now) (if (SwingUtilities/isEventDispatchThread)
                       (thunk)
                       (SwingUtilities/invokeAndWait thunk)))
  nil)

(defmacro do-swing
  "Executes body in the Swing event thread asynchronously. Returns
  immediately after scheduling the execution."
  [& body]
  `(do-swing* :later (fn [] ~@body)))

;; taken from clojure.contrib.str-utils2 1.2
(defn str-capitalize
  [s]
  (if (< (count s) 2)
    (.toUpperCase s)
    (str (.toUpperCase #^String (subs s 0 1))
         (.toLowerCase #^String (subs s 1)))))

;; taken from clojure.contrib.str-utils2 1.2
(defn str-split
  "Splits string on a regular expression. Optional argument limit is
   the maximum number of splits."
  ([#^String s re] (seq (.split re s)))
  ([#^String s re limit] (seq (.split re s limit))))

;; taken from clojure.contrib.str-utils2 1.2
(defn #^String str-upper-case
  "Converts string to all upper-case."
  [#^String s]
  (.toUpperCase s))

(defn kw-to-setter [kw]
  (symbol (apply str "set" (map str-capitalize (str-split (name kw) #"-")))))

(defn group-container-args [args]
  (reduce 
   (fn [{options :options kw :kw state :state :as r} arg]
     (cond
      (= state :forms)
      (update-in r [:forms] conj arg)
      kw
      (assoc r :options (assoc options kw arg) :kw nil)
      (keyword? arg)
      (assoc r :kw arg)
      (vector? arg)
      (assoc r :bindings arg :state :forms)))
   {:options {} :kw nil :state :options :forms []} args))

(defn remove-known-keys [m ks]
  (reduce dissoc m ks))

(defn has-index? [seq idx]
  (and (>= idx 0) (< idx (count seq))))

(defn icon-setters [names opts]
  (if opts
    (remove 
     nil?
     (map
      (fn [name] 
	(when-let [icon (opts name)] 
	  `(.  ~(kw-to-setter name) (.getImage (ImageIcon. ~icon))))) names))))

(defn auto-setters [cl known-kws opts]
  (if opts
    (map (fn [[a v]] (list 
		      '. 
		      (kw-to-setter a) 
		      (if (keyword? v)
			`(. ~cl ~(symbol (str-upper-case (name v))))
			v)))
	 (remove-known-keys opts known-kws))))

(defn insert-at [seq idx item]
  (concat (take idx seq) [item] (drop idx seq)))

(defn drop-nth [seq idx]
  (concat (take idx seq) (drop (inc idx) seq)))

(defmacro add-listener [obj add-fn listener-class & events]
  `(doto ~obj
     (~add-fn
      (proxy [~listener-class] []
	~@events))))

(defmacro add-action-listener [obj [[event] & code]]
  `(add-listener ~obj .addActionListener ActionListener
	(actionPerformed [~event]
			  ~@code)))

(defn <3 [love & loves] 
  (loop [l (str "I love " love) loves loves]
    (let [[love & loves] loves]
      (if (nil? love)
	(str l ".")
	(if (empty? loves)
	  (str l " and " love ".")
	  (recur (str l ", " love) loves))))))
