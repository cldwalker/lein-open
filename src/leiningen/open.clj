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

(defn- unpack [path]
  (let [jar-dir (->> path .getName (re-find #"(.*)\.jar") second)
        jar-dir (io/file lein-open-home jar-dir)
        commands ["unzip" "-d" (.getPath jar-dir) (.getPath path)]]
    (.mkdirs jar-dir)
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