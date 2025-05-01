# Project – Multi-Agent System (English)

### Author
- ***Eldis YMERAJ***
- `22408331`

---

## Project Description 
Implementation of a multi-agent system using the JADE library to simulate a production workshop.

## Project Structure
- `src/` : Contains Java source files.
- `lib/` : JADE library.

## Running the Project
```bash
mkdir build doc
javac -cp ".:lib/jade.jar" -d build src/*.java
javadoc -d doc -sourcepath src -classpath "lib/jade.jar" src/*.java
java -cp ".:lib/jade.jar:build" Main
```
## Remove Compiled Files and Documentation
```bash
   rm -rf build doc
   rm -f APDescription.txt MTPs-Main-Container.txt
```

---
# Projet - Système Multi-Agents (Français)
### Auteur
- ***Eldis YMERAJ***
- `22408331`

---
## Description du projet 
Implémentation d'un système multi-agent avec la bibliothèque JADE pour simuler un atelier de production.

## Structure du Projet
- `src/` : Contient les fichiers source Java.
- `lib/` : Bibliothèque JADE.
## Lancement du projet
```bash
mkdir build doc
javac -cp ".:lib/jade.jar" -d build src/*.java
javadoc -d doc -sourcepath src -classpath "lib/jade.jar" src/*.java
java -cp ".:lib/jade.jar:build" Main
```
---

## Supprimer les fichiers compilés et la documentation 

```bash
   rm -rf build doc
   rm -f APDescription.txt MTPs-Main-Container.txt
```
