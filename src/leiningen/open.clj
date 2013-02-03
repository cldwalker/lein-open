(ns leiningen.open
  (:require [leiningen.core.main :as main]
            [clojure.java.io :as io]
            [clojure.java.shell :as sh]))

(def maven-repository [(System/getProperty "user.home") ".m2" "repository"])
(def lein-open-home (io/file (System/getProperty "user.home") ".lein-open"))

(defn- maven-file [& args]
  (apply io/file (concat maven-repository args)))

(defn- jar-file [group artifact version]
  (maven-file group artifact version (format "%s-%s.jar" artifact version)))

;; from https://github.com/Raynes/fs/blob/master/src/me/raynes/fs.clj
(defn- delete-dir
  "Delete a directory tree."
  [root]
  (when (.isDirectory root)
    (doseq [path (map #(io/file root %) (.list root))]
      (delete-dir path)))
  (.delete root))

(defn- unpack [path]
  (let [jar-dir (->> path .getName (re-find #"(.*)\.jar") second
                     (io/file lein-open-home))
        commands ["unzip" "-d" (.getPath jar-dir) (.getPath path)]]
    (.mkdirs jar-dir)
    (when (.exists jar-dir) (delete-dir jar-dir))
    (apply sh/sh commands)
    (println (.getPath jar-dir))))

(defn open
  "Unpacks a project's dependency in ~/.lein-open/:name."
  [project dependency]
  (if-let [dep (->> project
                    :dependencies
                    (filter (fn [[full-name version]] (= (name full-name) dependency)))
                    first)]
    (let [[full-name version] dep
          group (namespace full-name)
          artifact (name full-name)
          path (jar-file group artifact version)]
      (if (.exists path)
        (unpack path)
        (main/abort (format "No jar exists for %s." dependency))))
    (main/abort (format "Dependency %s not found in this project." dependency))))