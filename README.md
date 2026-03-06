# app-android

# Comandos Git Esenciales para Trabajo en el equipo

Este documento resume los comandos más importantes para gestionar ramas, commits y colaboraciones en un proyecto.
Es indispensable tener instalado git en tu dispositivo

---

```bash

##------------------------------------------------------------------------
## 🪴 Configuración inicial antes de utilizar git en tus proyecto
##------------------------------------------------------------------------
# Configurar usuario y correo para tu git local (Nombre de usuario y correo que esta vinculado a GitHub)
git config --global user.name "Tu Nombre"
git config --global user.email "tu@email.com"

# Ver las configuraciones de tu usuario actual, si configuraste mal puedes repetir los comandos anteriores para remplazar tus datos
git config --global user.name
git config --global user.email

# Ver configuración general de git en tu dispositivo
git config --list




##------------------------------------------------------------------------
## 🎉 Empezamos a utilizar Git ✨
##------------------------------------------------------------------------
# Inicializar tu propio proyecto local para trabajar con sistema de versiones git
# Tienes que estar dentro de tu proyecto ejemplo: ruta/marlo/mi_proyecto, clic derecho en tu carpeta, clic git bash u otra terminal, si no te sale busca en más opciones.
git init

# Vincular tu proyecto local inicializado con git a un repositorio el la nube (GitHub)/ Tienes que tener creado el repositorio en GitHub
git remote add origin https://github.com/usuario/mi-proyecto.git

## Clonar un proyecto de un repositorio remoto (Esto ya trae git inicializado en el proyecto)
git clone https://github.com/usuario/este-es-un-repositorio-remoto.git

## Verificar si ya tienes vinculada tu rama a alguna rama en la nube 
git remote -v
    
## Si quieres desvincular la rama actual con la que esté vinculada en GitHub (en este caso eliminamos la vinculación de la rama main)
git remote remove origin





##------------------------------------------------------------------------
## 🔍 📄 Control de cambios (Esto funciona cuando ya inicializaste tu proyecto con git init o clonaste un nuevo repositorio en tu dispositivo)
##------------------------------------------------------------------------
# Ver cambios que realizaste dentro de el proyecto, ejemplo cambiaste algo en un archivo, eliminaste o agregaste archivos.
git status

# Alistar los cambios que tienes para un commit, recuerda que un commit es una copia de tu proyecto, estos archivos que agregues si incluirás en esta copia
git add archivo.txt  # Solo se añadirá el archivo.txt
git add .   # Añadir todos los archivos que se han agregado o modificado

# Crear commit, ahora se creará la copia del proyecto, esta copia o commit tiene un id específico como este a1b2c3d
git commit -m "Mensaje descriptivo del de la copia"

# Ver historial de commits general
git log

# ver historial resumido (1 línea por commit)
git log --oneline

# Ver los commits, ramas y merges (merge es la fusion entre ramas), este comando muestra a más detallado todo el historial de commits
git log --graph --oneline --all

#Muestra información completa de un commit específico.
# Incluye:
# Autor y fecha
# Mensaje del commit
# Cambios realizados en los archivos
git show a1b2c3d # Este es el id del commit que quieres visualizar a1b2c3d

# Comparar cambios entre commits
git diff a1b2c3d4 a2n2c3d4




##------------------------------------------------------------------------
## ❌ 📄 ELIMINAR COMMITS
##------------------------------------------------------------------------
# (Opción 1) Eliminar el último o los últimos commit realizado
# Reset duro para “volver exactamente al commit antiguo”
# Si quieres borrar todo lo posterior al commit y regresar al estado exacto de un commit específico.
# Úsalo solo si estás seguro de que quieres descartar lo más reciente.
git reset --hard a1b2c3d

#Luego fuerza el push para que este cambio se realize en la nube (esto es si es que lo tienes vinculado el repositorio local al repositorio remoto)
git push --force origin nombre-de-la-rama  # Colocar el nombre de la rama en la que te encuentras trabajando

# Opción 2: Eliminar commits que no son los últimos
# Si quieres borrar un commit intermedio (por ejemplo el 3º de 5 commits), usa rebase.
#HEAD~5 significa: "quiero revisar los últimos 5 commits".
#Se abrirá un editor con una lista de esos commits.
#Para eliminar un commit intermedio, borrarlo de la lista o cámbialo por drop.
git rebase -i HEAD~5

#Si ya estaban esos commits en remoto, termina forzando los cambios de los commits eliminados en la nube, esto es lo mismo que el la opción 1:
git push --force origin nombre-de-la-rama

## MUY IMPORTANTE: Cualquier integrante de equipo que ya haya hecho git pull( git pull es cuando tiran o jalan los últimos cambios de el repositorio remoto a su copia local) de esa rama tendrá que forzar su repositorio local para alinearse con lo que se tiene en el repositorio remoto, o de lo contrario habrá conflictos de cambios entre integrantes del equipo.

#Comando para tu/s compañeros de equipo para forzar sus cambios del repositorio remoto. Para que hagan esto debes comunicarte con tus compañeros para que puedan actualizar sus repositorio remoto ya que hiciste un cambio en el repositorio remoto.
git fetch origin 
git reset --hard origin/nombre-de-la-rama #nombre de la rama que modificaste commits

# (Todo esto tienes que tener en cuenta cunado trabajan varias personas en una solo en una rama, lo más recomendable es trabajar cada persona en un rama del repositorio de manera individual)




##------------------------------------------------------------------------
## 🚘 📄 MOVERSE entre COMMITS / entre copias
##------------------------------------------------------------------------
# Ir a una copia anterior y continuar desde allí hacia adelante.
# Los cambios posteriores siguen en tu área de staging no se borra asi que puedes regresar para esos commits.
# Todo lo que hagas después (commits nuevos) se guardará en esa rama.
# úsalo cuando:
# Si cometiste errores en los últimos commits y quieres rehacerlos.
# Quieres combinar cambios o modificar mensajes de commits recientes.
# Pros: seguro, todo sigue en tu rama y puedes seguir trabajando.
# Contras: solo sirve para últimos commits, no para commits muy antiguos intermedios sin usar rebase.
git reset --soft  a2n2c3d4

# Ir a una copia anterior, sin especificar en que rama en la que te encuentras.
# Puedes mirar, compilar, probar o inspeccionar el código de ese commit.
# Es como estar de supervisor: puedes ver y probar, pero tus cambios no afectan la línea principal hasta que decidas moverlos a una rama.
# Cuándo usarlo: para los mismos casos que el comando anterior
# Si haces commits aquí, no se guardarán en ninguna rama a menos que crees una nueva.
git switch --detach  a2n2c3d4 # → solo estás “visitando” un commit
# Si Estas utilizando este comando --detach , desde un commit en específico puedes crear una nueva rama para poder seguir trabajando en base a ese commit
git switch -c nombre-nueva-rama a1b2c3d4




##------------------------------------------------------------------------
## 🌿 Trabajar con ramas
##------------------------------------------------------------------------
git switch -c feature/nueva-funcionalidad   # Crear y moverte a la rama feature/nueva-funcionalidad en tu git local
git push -u origin feature/nueva-funcionalidad  # Subir la rama local a remoto, esto ya enlaza automáticamente para que luego puedas solo utilizar git push o git pull y ya no especifiques la rama nuevamente.
git push -u origin main # Ejemplo en este caso estoy haciendo push a la rama principal (Esto normalmente se utiliza despues de vincular el origen del poryecto ocn la nueve y esto es para que se vincule la rama local con la remota)
git push         # Envía tus cambios que hiciste en tu rama al la nube (Esto actualizar los cambios de tu repositorio de GitHub)
git pull         # Jalar los cambios de la rama remota del repositorio de GitHub a tu rama local.
#git push, funciona si eres administrador del repositorio su estas colaborando no funcionara, tienes que solicitar un PR (Pull Request)

git pull origin main --rebase  #Si tienes commits locales  que no tienes en remoto y commits remotos que no tienes en locales (Si quieres mantener un historial limpio, usa rebase)
git push origin main #Luego, si no hubo conflictos, haz push de tus cambios:

#Lista ramas existentes
git branch        # ramas que tienes en tu repositorio local (Dispositivo)
git branch -r     # ramas que tienes en tu repositorio en remoto (GitHub)
git branch -a     # todas a la vez

# Moverse a una rama en en concreto
git switch nombre-de-rama

#Renombrar una rama
git branch -m nombre-antiguo nuevo-nombre




##------------------------------------------------------------------------
## ❌ 🌿 Eliminar ramas
##------------------------------------------------------------------------
## Eliminar rama local
git branch -d feature/nueva-funcionalidad

# Eliminar rama remota
git push origin --delete feature/nueva-funcionalidad (Recuerda, si trabajas varias personas en un sola rama, y realizas algún cambio en el repositorio remoto avisar a tu equipo para que puedan actualizar sus copias de sus ramas locales)




##------------------------------------------------------------------------
## 🌿 🌀 Fusionar ramas (Estando en main fusionamos los cambios de la develop develop)
## ------------------------------------------------------------------------
<<<<< SOLO PARA COLABORADORES AUTORIZADOS >>>
# ESTO LO HACE EL ADMINISTRADOR DE UN REPOSITORIO, NO ES RECOMENDABLE QUE LOS COLABORADORES FUSIONEN DIRECTAMENTE EN EL REPO REMOTO, EN ESPECIAL EN LA RAMA DE PRODUCION (main) O A LA RAMA DE DESARROLLO (develop), CADA COLABORADOR DEBE TRABAJAR EN SU RAMA HACIENDO UNA TAREA ESPECIFICA, CUANDO LA TAREA SE TERMINE SE SOLICITA Al ADMINISTRADOR QUE FUSIONE SU RAMA CON LA RAMA PRINCIPAL, en este caso la rama develop (Desarrollo)
#También puedes crear y fusionar ramas pero en tu copia de repositorio local o si trabajando en un tu propio repositorio remoto, ya que allí si eres adminsitrador, cuando trabajas en equipo tienes que solicitar permisos al adminsitrador para que fusione tu rama en la que estuvite trabajando, a esto se le conoce hacer un pull reqest.

# Ejemplo. Cuando puedes funcionar ramas, clonas un repositorio o estas trabajando en tu propio proyecto y quieres integrar una nueva feature(funcionalidad). Implementar un login en el proyecto, primiero creas tu rama llamada feature/login y a la vez puedes crear dos ramas apartir de esta.
#    feature/login
#     ├─ feature/login-ui       ← mejoras en la interfaz del login
#     ├─ feature/login-backend  ← cambios en la lógica de autenticación

# Para fusionar haríamos lo siguiente:
git fetch origin feature/login # Primero actualiza la referencias de tu rama local con la rama de la nube de GitHub (Si clonaste el repo o tienes tu repositorio en la nube)
git switch feature/login # Cambiamos a la rama principal, feature/login (Aquí es donde se fusionara las 2 rama hijas)
git merge feature/login-ui  # Realizamos la fusión o integración de los cambios a la rama feature/login-ui a la rama padre. Esto traerá todos los commits de feature/login-ui  a  eature/logino.
git merge feature/login-backend #lo mismo sería pra fusionar la rama feature/login-backend al la rama principal

#  <<<<< Si no hay conflictos, se crea un commit de fusion automáticamente por cada merge >>>>>
# y si los hay, tendrás que corregir y hacer nuevamente la fusión de tus ramas


```



# Cómo realizar un Pull Request siendo colaborador de solo lectura (Lo más común profesionalmente)

Si eres un colaborador con **permiso de solo lectura**, no puedes hacer push directo al repositorio principal, pero **sí puedes contribuir mediante un pull request**.

### 1. Hacer un fork del repositorio

-   Ve al repositorio principal en GitHub en el que deseas colaborar y haz clic en **Fork** ,está en la parte superior del repositorio.
-   Esto creará una copia del repositorio en tu cuenta de GitHub en el cual si serás administrador.

### 2. Clonar tu fork en tu máquina local (es decir tener una copia del proyecto original)

```bash
git clone https://github.com/marlodev/nombre-del-repositorio.git
```

### 3. Configurar tu repositorio clonado con el repositorio remoto original.

Esto permite mantener tu fork(copia) este vinculado con el repositorio original de GitHub
Con **upstream** específicas que tu fork es copia de repositorio que está una dirección específica en GitHub

```bash
git remote add upstream https://github.com/organizacion/nombre-del-repositorio-original.git
git fetch upstream  # Trae todos los cambios del repositorio remoto upstream 
git switch main  # Cambia a tu rama principal local
git merge upstream/main # Actualiza tu rama principal con los cambios del upstream
```

### 4. Crear una nueva rama para tu cambio/tarea/ o funcionalidad en la que vas a trabajar.

Siempre trabaja en una rama nueva para tu feature o corrección:

```bash
git switch -c mi-rama-feature/login
```

### 5. Realizar los cambios y empieza hacer tus commits de tu rama.

```bash
git add .
git commit -m "Descripción clara del cambio"
```

### 6. Subir la rama a tu fork

```bash
git push origin mi-rama-feature/login
```

### 7. Solicitar Pull request

-   Ve a tu fork en GitHub a tu copia del proyecto.
-   Haz clic en **Compare & pull request**.
-   Selecciona la rama del repositorio original (generalmente `develop`) como destino y tu rama como origen (rama en donde estuviste trabajando tus feature).
-   Describe tus cambios claramente y envía el pull request.

### 8. Esperar revisión y merge

-   Los administradores del repositorio revisarán tu PR.
-   Pueden pedir cambios antes de hacer el merge.

---

💡 **Tips útiles**

-   Mantén tu fork actualizado regularmente con `upstream` para evitar conflictos.
-   Usa ramas separadas para cada PR, nunca trabajes directamente en `main` lo ideal crea tu rama nueva para tu feature y desde esa rama haces Pull request a la rama develop.

