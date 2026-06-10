import java.util.*;
import java.util.function.Consumer;

class User {
    private final String name;
    private final Consumer<String> notificationHandler;

    public User(String name, Consumer<String> notificationHandler) {
        this.name = name;
        this.notificationHandler = notificationHandler;
    }

    public String getName() {
        return name;
    }

    public void update(String message) {
        notificationHandler.accept("[" + name + "] Notificação recebida: " + message);
    }
}

class StreamingService {
    private final Map<String, List<User>> observersByGenre = new HashMap<>();

    public void subscribe(String genre, User user) {
        observersByGenre.computeIfAbsent(genre.toLowerCase(), k -> new ArrayList<>()).add(user);
        System.out.println("-> " + user.getName() + " agora está inscrito em: " + genre);
    }

    public void unsubscribe(String genre, User user) {
        List<User> users = observersByGenre.get(genre.toLowerCase());
        if (users != null && users.remove(user)) {
            System.out.println("-> " + user.getName() + " CANCELOU a inscrição em: " + genre);
        } else {
            System.out.println("-> " + user.getName() + " não estava inscrito no gênero " + genre);
        }
    }

    public void addMovie(String title, String genre) {
        String normalizedGenre = genre.toLowerCase();
        System.out.println("\n=== [LANÇAMENTO] Novo título na plataforma! ===");
        System.out.println("Filme/Série: " + title + " | Gênero: " + genre);
        System.out.println("===============================================");
        
        List<User> users = observersByGenre.get(normalizedGenre);
        if (users == null || users.isEmpty()) {
            System.out.println("Nenhum usuário inscrito para o gênero " + genre + ".");
        } else {
            users.forEach(user -> user.update("O filme '" + title + "' acabou de ser adicionado!"));
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StreamingService netflix = new StreamingService();
        
        Consumer<String> defaultNotification = message -> System.out.println("\u001B[32m" + message + "\u001B[0m");

        Map<Integer, User> clients = new HashMap<>();
        clients.put(1, new User("Alice", defaultNotification));
        clients.put(2, new User("Bob", defaultNotification));
        clients.put(3, new User("Charlie", defaultNotification));
        clients.put(4, new User("David", defaultNotification));
        clients.put(5, new User("Eva", defaultNotification));

        int option = 0;
        while (option != 4) {
            System.out.println("\n-------------------------------------------");
            System.out.println("       SISTEMA DE NOTIFICAÇÕES - MENU      ");
            System.out.println("-------------------------------------------");
            System.out.println("1. Inscrever Cliente em um Gênero");
            System.out.println("2. Cancelar Inscrição de Cliente");
            System.out.println("3. Lançar Novo Filme (Notificar)");
            System.out.println("4. Sair");
            System.out.print("Escolha uma opção: ");
            
            try {
                option = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                option = 0;
            }

            switch (option) {
                case 1 -> {
                    User userSub = escolherCliente(scanner, clients);
                    if (userSub != null) {
                        System.out.print("Digite o gênero (ex: Ação, Comédia, Terror, Anime, Ficção Científica): ");
                        String genre = scanner.nextLine();
                        netflix.subscribe(genre, userSub);
                    }
                }
                case 2 -> {
                    User userUnsub = escolherCliente(scanner, clients);
                    if (userUnsub != null) {
                        System.out.print("Digite o gênero que deseja cancelar: ");
                        String genre = scanner.nextLine();
                        netflix.unsubscribe(genre, userUnsub);
                    }
                }
                case 3 -> {
                    System.out.print("Digite o Título do Filme/Série: ");
                    String title = scanner.nextLine();
                    System.out.print("Digite o Gênero deste Filme: ");
                    String genre = scanner.nextLine();
                    netflix.addMovie(title, genre);
                }
                case 4 -> System.out.println("Encerrando o sistema. Até mais!");
                default -> System.out.println("Opção inválida! Tente novamente.");
            }
        }
        scanner.close();
    }

    private static User escolherCliente(Scanner scanner, Map<Integer, User> clients) {
        System.out.println("\nEscolha um dos 5 clientes abaixo:");
        clients.forEach((id, user) -> System.out.println(id + ". " + user.getName()));
        System.out.print("Digite o número do cliente: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            if (clients.containsKey(id)) {
                return clients.get(id);
            }
        } catch (NumberFormatException e) {
        }
        
        System.out.println("Cliente inválido.");
        return null;
    }
}