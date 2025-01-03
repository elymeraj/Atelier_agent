# Devoir Maison - Système Multi-Agents
### Auteur
- ***Eldis YMERAJ***
- `22408331`


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