package com.example.ui

data class QuickTrenchPrompt(
    val id: String,
    val shortLabel: String,
    val categoryBadge: String,
    val fullMessage: String
)

data class TabooValidationCard(
    val id: String,
    val tabooTitle: String,
    val rawFeelingQuote: String,
    val validationBody: String,
    val effortRecognition: String,
    val microRegulation: String,
    val chatStarterPrompt: String
)

data class MicroRegulationExercise(
    val id: String,
    val title: String,
    val subtitle: String,
    val durationSeconds: Int = 60,
    val inhaleSec: Int = 4,
    val holdSec: Int = 4,
    val exhaleSec: Int = 6,
    val steps: List<String>,
    val closingAffirmation: String
)

data class CrisisHotline(
    val country: String,
    val serviceName: String,
    val phoneNumber: String,
    val availability: String
)

object CaregiverCatalog {

    val avatarNameSuggestions = listOf("Alma", "Noah", "Luz", "Gael", "Elena", "Sora")

    val trenchContexts = listOf(
        "Esperando en sala de quimio",
        "Turnos de noche y falta de sueño",
        "Lidiando en soledad mientras la familia opina",
        "Manejando trámites, recetas y traslados",
        "Agotado/a de fingir fortaleza frente a todos"
    )

    val quickTrenchPrompts = listOf(
        QuickTrenchPrompt(
            id = "invisible",
            shortLabel = "Nadie pregunta cómo estoy yo",
            categoryBadge = "Invisibilidad",
            fullMessage = "Todo el mundo llama para preguntar por la evolución médica y los estudios, pero nadie me pregunta cómo estoy yo ni si dormí algo anoche. Me siento completamente invisible."
        ),
        QuickTrenchPrompt(
            id = "rabia_hermanos",
            shortLabel = "Rabia con mi familia / hermanos",
            categoryBadge = "Rabia familiar",
            fullMessage = "Siento muchísima rabia con mis hermanos y mi familia. Ellos hacen su vida normal y solo aparecen de visita cinco minutos, mientras todo el peso diario recae sobre mi espalda."
        ),
        QuickTrenchPrompt(
            id = "culpa_terminar",
            shortLabel = "Culpa por querer que esto termine",
            categoryBadge = "Emoción tabú",
            fullMessage = "Me siento una mala persona porque hoy pensé que solo quiero que todo este proceso termine ya. Estoy exhausto/a y me mata la culpa por sentir esto."
        ),
        QuickTrenchPrompt(
            id = "harto_cliches",
            shortLabel = "Harto/a de que me digan «sé fuerte»",
            categoryBadge = "Sin clichés",
            fullMessage = "Si una persona más me dice «sé fuerte», «ánimo» o que «Dios da sus peores batallas a sus mejores soldados», voy a gritar. No quiero ser un soldado, estoy agotado/a."
        ),
        QuickTrenchPrompt(
            id = "sala_quimio",
            shortLabel = "Agotado/a en la espera de quimio",
            categoryBadge = "En la trinchera",
            fullMessage = "Llevo horas sentado/a esperando en el hospital entre la quimio, el manejo y los papeles. Tengo el cuello duro y la cabeza embotada."
        ),
        QuickTrenchPrompt(
            id = "miedo_medico",
            shortLabel = "Angustia por el tratamiento",
            categoryBadge = "Incertidumbre",
            fullMessage = "No sé si la quimio está haciendo efecto o qué significan los síntomas nuevos y no puedo parar de darle vueltas a la cabeza."
        )
    )

    val physicalStates = listOf(
        "Agotamiento físico total",
        "Cuello, espalda y mandíbula tensos",
        "Acumulando noches sin dormir",
        "Hoy casi ni comí ni tomé agua",
        "Con dolor de cabeza por estrés",
        "Mi cuerpo aguanta en piloto automático"
    )

    val emotionalStates = listOf(
        "Con rabia hacia familiares ausentes",
        "Invisible: nadie me ve a mí",
        "Con culpa por mis pensamientos",
        "Sobrepasado/a por tanta exigencia",
        "Harto/a de las frases hechas",
        "Triste y sin espacio para llorar"
    )

    val trenchEfforts = listOf(
        "Acompañar horas en sala de quimio",
        "Manejar, estacionar y resolver traslados",
        "Pelear con trámites, autorizaciones y farmacia",
        "Cuidar de noche y vigilar cada síntoma",
        "Sostener el ánimo en casa callando lo mío",
        "Responder partes médicos a toda la familia"
    )

    val tabooCards = listOf(
        TabooValidationCard(
            id = "card_rabia",
            tabooTitle = "Rabia y celos hacia hermanos o familiares",
            rawFeelingQuote = "«Ellos siguen teniendo fines de semana, trabajo y descanso; yo me quedé atrapado/a sosteniendo todo.»",
            validationBody = "Sentir rabia o resentimiento hacia quienes opinan desde afuera sin poner el cuerpo es una reacción sana ante la injusticia. No tienes obligación de «comprenderlos» ni de justificar su ausencia.",
            effortRecognition = "Cada turno, cada traslado y cada hora de espera que tú absorbes les está permitiendo a ellos seguir con su rutina. Tu desgaste es real.",
            microRegulation = "Suelta los dientes apretados ahora mismo y deja caer los hombros lejos de las orejas.",
            chatStarterPrompt = "Siento mucha rabia y resentimiento porque mis familiares siguen con su vida normal mientras yo cargo con todo el cuidado diario."
        ),
        TabooValidationCard(
            id = "card_terminar",
            tabooTitle = "Desear en secreto que todo el proceso termine",
            rawFeelingQuote = "«A veces solo quiero que esto se acabe de una vez, y enseguida me siento un monstruo por pensarlo.»",
            validationBody = "Querer que termine el sufrimiento, la alerta perpetua y el hospital NO significa que no ames a tu familiar. Significa que tu sistema nervioso llegó al límite de lo que un ser humano puede tolerar.",
            effortRecognition = "Estar al pie del cañón día y noche mientras sientes ese cansancio demuestra una lealtad inmensa.",
            microRegulation = "Apoya tu palma en el centro del pecho y repite internamente: «Estoy exhausto/a, no soy culpable».",
            chatStarterPrompt = "Hoy pensé que quiero que todo este proceso termine ya para recuperar mi vida y siento muchísima culpa."
        ),
        TabooValidationCard(
            id = "card_invisibilidad",
            tabooTitle = "El dolor de volverse invisible",
            rawFeelingQuote = "«Cuando llegan visitas o llaman por teléfono, yo soy solo el recepcionista que da el informe médico.»",
            validationBody = "La invisibilidad del cuidador primario duele profundamente. Tú también atravesaste un terremoto vital desde el día del diagnóstico, aunque no tengas el catéter puesto.",
            effortRecognition = "Coordinar citas, recordar medicaciones y contener el miedo ajeno es un trabajo invisible que sostiene todo el tratamiento.",
            microRegulation = "Bebe medio vaso de agua despacio. Hoy este espacio pregunta primero por ti.",
            chatStarterPrompt = "Me duele ser invisible para todo el entorno; solo me hablan para pedirme el reporte médico del paciente."
        ),
        TabooValidationCard(
            id = "card_cliches",
            tabooTitle = "Rechazo a los clichés de «guerreros y soldados»",
            rawFeelingQuote = "«No quiero que me digan 'sé fuerte' ni 'todo pasa por algo'. Esto es injusto y agotador.»",
            validationBody = "Las frases hechas suelen decirlas quienes no saben sostener la incomodidad de tu dolor. Aquí no tienes que ser positivo/a ni encontrarle una lección espiritual al agotamiento.",
            effortRecognition = "Seguir manejando, esperando en pasillos y resolviendo problemas aún sin esperanza impostada es el verdadero sostén.",
            microRegulation = "Exhala por la boca con un suspiro largo para soltar la máscara de «fuerte».",
            chatStarterPrompt = "Estoy harto/a de que la gente me repita frases hechas como «sé fuerte» o «todo va a salir bien» sin ver lo destruido/a que estoy."
        )
    )

    val microExercises = listOf(
        MicroRegulationExercise(
            id = "resp_quimio",
            title = "Respiración en Sala de Espera (4-4-6)",
            subtitle = "Para bajar el cortisol en la silla del hospital sin que nadie a tu alrededor lo note.",
            durationSeconds = 60,
            inhaleSec = 4,
            holdSec = 4,
            exhaleSec = 6,
            steps = listOf(
                "Apoya ambos pies planos en el suelo y descruza las piernas.",
                "Inhala suave por la nariz contando 4 segundos.",
                "Sostén el aire sin apretar la garganta durante 4 segundos.",
                "Suelta el aire muy lento en 6 segundos, dejando caer el peso de tu espalda en el respaldo."
            ),
            closingAffirmation = "Tómate un minuto antes de volver a responder mensajes. Aquí sigo contigo."
        ),
        MicroRegulationExercise(
            id = "soltar_auto",
            title = "Pausa de 60s en el Auto o Pasillo",
            subtitle = "Para descargar la tensión muscular después de manejar, discutir o hacer trámites.",
            durationSeconds = 60,
            inhaleSec = 4,
            holdSec = 2,
            exhaleSec = 6,
            steps = listOf(
                "Separa los dientes superiores de los inferiores; deja la lengua floja.",
                "Sube los hombros hacia las orejas al inhalar en 4 segundos.",
                "Déjalos caer de golpe al exhalar largo en 6 segundos.",
                "Abre las manos y estira los dedos que han estado apretando el volante o carpetas."
            ),
            closingAffirmation = "Lo que sostuviste hoy fue enorme. No tienes que resolver las próximas 24 horas en este minuto."
        ),
        MicroRegulationExercise(
            id = "pausa_culpa",
            title = "Soltar la Culpa del Cuidador",
            subtitle = "Un minuto de contención física cuando te castigas por sentir rabia o hartazgo.",
            durationSeconds = 60,
            inhaleSec = 4,
            holdSec = 3,
            exhaleSec = 5,
            steps = listOf(
                "Coloca una mano cálida sobre tu esternón o tu cuello.",
                "Reconoce el cansancio real de tus ojos y tu espalda sin pelear con él.",
                "Al inhalar piensa: «Mi agotamiento es real».",
                "Al exhalar piensa: «Sentir rabia o cansancio no borra todo lo que hago»."
            ),
            closingAffirmation = "Eres un ser humano agotado en una trinchera difícil, no una máquina. Respira."
        )
    )

    val crisisHotlines = listOf(
        CrisisHotline(
            country = "España",
            serviceName = "Línea 024 de Atención a la Conducta Suicida",
            phoneNumber = "024",
            availability = "Gratuito · Confidencial · 24 horas"
        ),
        CrisisHotline(
            country = "México",
            serviceName = "Línea de la Vida",
            phoneNumber = "8009112000",
            availability = "Gratuito · 24 horas (800 911 2000)"
        ),
        CrisisHotline(
            country = "EE.UU. / Puerto Rico / Internacional",
            serviceName = "Red Nacional de Prevención del Suicidio (Español)",
            phoneNumber = "988",
            availability = "Gratuito · 24 horas (Llamada o texto al 988)"
        ),
        CrisisHotline(
            country = "Argentina",
            serviceName = "Centro de Asistencia al Suicida",
            phoneNumber = "135",
            availability = "Gratuito desde CABA/GBA · 24 horas"
        ),
        CrisisHotline(
            country = "Colombia",
            serviceName = "Línea 106 de Salud Mental / 192",
            phoneNumber = "106",
            availability = "Gratuito · 24 horas"
        ),
        CrisisHotline(
            country = "Chile",
            serviceName = "Línea Prevención del Suicidio MINSAL",
            phoneNumber = "*4141",
            availability = "Gratuito desde celulares · 24 horas"
        )
    )
}
