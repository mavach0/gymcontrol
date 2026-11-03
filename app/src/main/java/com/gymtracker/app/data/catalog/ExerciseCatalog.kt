package com.gymtracker.app.data.catalog

import com.gymtracker.app.domain.model.Exercise
import com.gymtracker.app.domain.model.MuscleGroup

/**
 * Catálogo estático de ejercicios. Mantener los datos fuera del repositorio permite
 * sustituir esta fuente por una base de datos o una API sin tocar la lógica de negocio.
 */
object ExerciseCatalog {

    val all: List<Exercise> = listOf(
        // PECHO
        Exercise(
            id = "1", name = "Press de Banca Plano", muscleGroup = MuscleGroup.CHEST,
            equipment = "Barra", defaultRestSeconds = 90,
            description = "Ejercicio compuesto para el pecho con barra. Acuéstate en el banco con los pies firmes en el suelo. Agarra la barra con un agarre ligeramente más ancho que los hombros. Baja la barra hasta el esternón de forma controlada y empuja hacia arriba extendiendo los codos por completo.",
            tips = listOf("Mantén los omóplatos retraídos y apretados durante todo el movimiento", "No rebotes la barra en el pecho", "Pies siempre en contacto con el suelo")
        ),
        Exercise(
            id = "2", name = "Press Inclinado con Mancuernas", muscleGroup = MuscleGroup.CHEST,
            equipment = "Mancuernas", defaultRestSeconds = 75,
            description = "Banco inclinado a 30-45°. Sostén las mancuernas a los lados del pecho con los codos a 75°. Empuja hacia arriba y adentro en arco hasta que los pesos casi se toquen, luego baja lentamente.",
            tips = listOf("No bloquees los codos al extender", "El ángulo del banco no debe superar 45°", "Controla la bajada en 2-3 segundos")
        ),
        Exercise(
            id = "3", name = "Aperturas en Polea (Cruce)", muscleGroup = MuscleGroup.CHEST,
            equipment = "Polea", defaultRestSeconds = 60,
            description = "De pie entre dos poleas altas. Agarra los cables y lleva los brazos hacia adelante y al centro del cuerpo en un arco. Mantén una leve flexión en los codos durante todo el movimiento para aislar el pecho.",
            tips = listOf("Inclínate ligeramente hacia adelante para mayor activación del pecho", "Espira al cruzar los cables", "Evita usar el impulso de la espalda")
        ),
        Exercise(
            id = "4", name = "Fondos en Paralelas", muscleGroup = MuscleGroup.CHEST,
            equipment = "Peso Corporal", defaultRestSeconds = 90,
            description = "Sujétate en las paralelas con brazos extendidos. Inclínate ligeramente hacia adelante y baja doblando los codos hasta que los hombros estén a la altura de los codos. Empuja hacia arriba para volver a la posición inicial.",
            tips = listOf("Inclínate más hacia adelante para activar más el pecho vs. tríceps", "No bajes demasiado para proteger los hombros", "Mantén el core apretado")
        ),

        // ESPALDA
        Exercise(
            id = "5", name = "Peso Muerto Convencional", muscleGroup = MuscleGroup.BACK,
            equipment = "Barra", defaultRestSeconds = 120,
            description = "El rey de los ejercicios compuestos. Con la barra sobre los pies, flexiona caderas y rodillas para agarrarla. Mantén la espalda neutra y empuja el suelo hacia abajo mientras extiendes caderas y rodillas simultáneamente hasta quedar de pie.",
            tips = listOf("Nunca redondees la zona lumbar", "La barra debe rozar las espinillas durante el ascenso", "Activa los dorsales jalándolos hacia los bolsillos")
        ),
        Exercise(
            id = "6", name = "Dominadas Pronas", muscleGroup = MuscleGroup.BACK,
            equipment = "Peso Corporal", defaultRestSeconds = 90,
            description = "Cuelga de la barra con agarre prono (palmas mirando al frente) a una anchura superior a los hombros. Retrae los omóplatos y jala el cuerpo hacia arriba hasta que el mentón pase la barra, luego baja lentamente.",
            tips = listOf("No balancees el cuerpo para subir", "Piensa en llevar los codos hacia los bolsillos traseros", "El pecho debe casi tocar la barra en la posición alta")
        ),
        Exercise(
            id = "7", name = "Remo con Barra Pendlay", muscleGroup = MuscleGroup.BACK,
            equipment = "Barra", defaultRestSeconds = 90,
            description = "Con la barra en el suelo, inclínate hasta que el torso quede casi paralelo al suelo. Jala la barra explosivamente hasta el abdomen y devuélvela al suelo entre cada repetición manteniendo la posición del torso.",
            tips = listOf("El torso debe estar casi horizontal (no 45°)", "Jala hacia el abdomen bajo, no hacia el pecho", "Cada rep parte desde el suelo para maximizar la fuerza")
        ),
        Exercise(
            id = "8", name = "Jalón al Pecho en Polea", muscleGroup = MuscleGroup.BACK,
            equipment = "Polea", defaultRestSeconds = 60,
            description = "Siéntate frente a la polea alta con los muslos bajo los rodillos. Agarra la barra larga con agarre prono y jálala hacia la parte alta del pecho inclinando ligeramente el torso hacia atrás. Sube lentamente con control.",
            tips = listOf("No te eches demasiado hacia atrás, usa dorsales no inercia", "Lleva los codos hacia abajo y atrás", "Extiende completamente los brazos en la parte alta")
        ),

        // PIERNAS
        Exercise(
            id = "9", name = "Sentadilla Trasera con Barra", muscleGroup = MuscleGroup.LEGS,
            equipment = "Barra", defaultRestSeconds = 120,
            description = "Con la barra sobre los trapecios, separa los pies al ancho de hombros con puntas ligeramente externas. Flexiona caderas y rodillas simultáneamente hasta que los muslos queden paralelos (o más bajos) al suelo, manteniendo el pecho erguido.",
            tips = listOf("Las rodillas deben seguir la dirección de los pies", "No permitas que las rodillas caigan hacia adentro", "Inhala bajando, exhala empujando")
        ),
        Exercise(
            id = "10", name = "Prensa de Piernas 45°", muscleGroup = MuscleGroup.LEGS,
            equipment = "Máquina", defaultRestSeconds = 90,
            description = "Siéntate en la máquina con los pies en la plataforma al ancho de hombros. Empuja la plataforma extendiendo las piernas sin bloquear las rodillas. Baja controladamente hasta 90° de flexión.",
            tips = listOf("No bloquees completamente las rodillas en la extensión", "Coloca los pies más arriba para más glúteo/femoral, más abajo para cuádriceps", "Mantén la espalda baja pegada al respaldo")
        ),
        Exercise(
            id = "11", name = "Curl Femoral Tumbado", muscleGroup = MuscleGroup.LEGS,
            equipment = "Máquina", defaultRestSeconds = 60,
            description = "Túmbate boca abajo en la máquina con los talones bajo el rodillo. Contrae los isquiotibiales para flexionar las rodillas y llevar los talones hacia los glúteos. Baja lentamente a la posición inicial.",
            tips = listOf("No arquees la espalda al subir", "El movimiento debe ser fluido y controlado", "Pausa 1 segundo en la contracción máxima")
        ),
        Exercise(
            id = "12", name = "Elevación de Gemelos en Máquina", muscleGroup = MuscleGroup.LEGS,
            equipment = "Máquina", defaultRestSeconds = 45,
            description = "Coloca los hombros bajo los apoyos de la máquina con la punta de los pies en el escalón. Elévate sobre las puntas de los pies al máximo, aguanta 1 segundo, y baja hasta sentir un estiramiento completo en los gemelos.",
            tips = listOf("Realiza el rango completo de movimiento para máxima activación", "No rebotas en el punto más bajo", "Pausa en la contracción para mayor intensidad")
        ),

        // HOMBROS
        Exercise(
            id = "13", name = "Press Militar con Barra", muscleGroup = MuscleGroup.SHOULDERS,
            equipment = "Barra", defaultRestSeconds = 90,
            description = "De pie (o sentado) con la barra a la altura del pecho, agarre prono ligeramente más ancho que los hombros. Empuja la barra verticalmente por encima de la cabeza hasta extender los brazos. Baja de forma controlada.",
            tips = listOf("Aprieta el glúteo y el core para estabilizar la columna", "No arquees excesivamente la espalda", "La barra debe trazar un camino vertical")
        ),
        Exercise(
            id = "14", name = "Elevaciones Laterales", muscleGroup = MuscleGroup.SHOULDERS,
            equipment = "Mancuernas", defaultRestSeconds = 60,
            description = "De pie con una mancuerna en cada mano. Eleva los brazos hacia los lados hasta la altura de los hombros con los codos ligeramente flexionados. Baja lentamente con control total.",
            tips = listOf("Inclina levemente las mancuernas como si vertieras agua para activar más el deltoides medio", "No uses impulso del torso", "El movimiento debe durar 2-3 segundos bajando")
        ),
        Exercise(
            id = "15", name = "Pájaros para Deltoides Posterior", muscleGroup = MuscleGroup.SHOULDERS,
            equipment = "Mancuernas", defaultRestSeconds = 60,
            description = "Inclínate hacia adelante hasta que el torso sea casi paralelo al suelo. Con las mancuernas colgando, eleva los brazos hacia los lados hasta la altura de los hombros manteniendo los codos ligeramente doblados.",
            tips = listOf("Retrae los omóplatos al subir", "No balancees el cuerpo", "Mantén la cabeza en posición neutra (no la levantes)")
        ),

        // BRAZOS
        Exercise(
            id = "16", name = "Curl de Bíceps con Barra Z", muscleGroup = MuscleGroup.ARMS,
            equipment = "Barra", defaultRestSeconds = 60,
            description = "De pie con la barra Z agarrada a una anchura de hombros, codos pegados al cuerpo. Flexiona los codos llevando la barra hacia los hombros contrayendo los bíceps al máximo. Baja lentamente.",
            tips = listOf("Mantén los codos completamente fijos", "No balancees el torso hacia atrás al subir", "Extiende completamente al bajar para el rango completo")
        ),
        Exercise(
            id = "17", name = "Press Francés con Mancuernas", muscleGroup = MuscleGroup.ARMS,
            equipment = "Mancuernas", defaultRestSeconds = 60,
            description = "Túmbate en el banco con las mancuernas sobre la cabeza, brazos verticales. Flexiona sólo los codos bajando las mancuernas hacia el lado de la cabeza y extiende para volver a la posición inicial.",
            tips = listOf("Los codos no deben moverse, sólo se flexionan", "Mantén las muñecas neutras", "Baja lentamente para evitar lesiones en el codo")
        ),
        Exercise(
            id = "18", name = "Extensiones de Tríceps en Polea", muscleGroup = MuscleGroup.ARMS,
            equipment = "Polea", defaultRestSeconds = 45,
            description = "De pie frente a la polea alta con la cuerda o barra. Con los codos pegados al cuerpo, extiende las manos hacia abajo hasta que los brazos queden totalmente rectos, separando ligeramente la cuerda al final.",
            tips = listOf("Los codos no deben moverse hacia atrás", "Gira las muñecas hacia afuera al final con la cuerda", "Controla el retorno sin soltar la tensión")
        ),
        Exercise(
            id = "19", name = "Curl Martillo", muscleGroup = MuscleGroup.ARMS,
            equipment = "Mancuernas", defaultRestSeconds = 60,
            description = "De pie con las mancuernas en agarre neutro (palmas enfrentadas). Flexiona los codos llevando las mancuernas hacia los hombros sin rotar la muñeca. Baja lentamente.",
            tips = listOf("Activa braquial y braquiorradial además del bíceps", "No balancees el torso", "Puede hacerse alternado o simultáneo")
        ),

        // CORE
        Exercise(
            id = "20", name = "Plancha Abdominal", muscleGroup = MuscleGroup.CORE,
            equipment = "Peso Corporal", defaultRestSeconds = 60,
            description = "Apoya los antebrazos y las puntas de los pies en el suelo formando una línea recta desde la cabeza hasta los talones. Contrae el abdomen y el glúteo para mantener la posición sin hundir la cadera.",
            tips = listOf("No levantes las caderas ni las hundas", "Respira de forma constante durante la posición", "Mira el suelo para mantener el cuello neutro")
        ),
        Exercise(
            id = "21", name = "Elevación de Piernas Colgado", muscleGroup = MuscleGroup.CORE,
            equipment = "Barra", defaultRestSeconds = 60,
            description = "Cuelga de una barra con agarre prono. Sin balancearte, eleva las piernas extendidas (o con rodillas flexionadas) hasta la altura de las caderas o más arriba. Baja de forma controlada.",
            tips = listOf("Evita el balanceo usando el impulso", "Para más dificultad, mantén las piernas rectas", "Exhala al subir las piernas")
        ),
        Exercise(
            id = "22", name = "Rueda Abdominal (Ab Wheel)", muscleGroup = MuscleGroup.CORE,
            equipment = "Accesorios", defaultRestSeconds = 60,
            description = "Arrodillado, sujeta la rueda con ambas manos. Rueda hacia adelante extendiendo el cuerpo cerca del suelo manteniendo el core contraído, y vuelve jalando con el abdomen sin colapsar la espalda.",
            tips = listOf("No dejes que la espalda baja se hunda", "Empieza con el rango de movimiento pequeño e incrementa progresivamente", "El retorno debe ser activo, controlado por el core")
        )
    )

    fun byId(id: String): Exercise? = all.firstOrNull { it.id == id }

    fun byIds(ids: List<String>): List<Exercise> = ids.mapNotNull(::byId)

    fun byNames(names: List<String>): List<Exercise> =
        names.mapNotNull { name -> all.firstOrNull { it.name.equals(name, ignoreCase = true) } }
}
