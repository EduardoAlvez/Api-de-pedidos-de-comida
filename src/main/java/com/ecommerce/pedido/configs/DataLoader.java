package com.ecommerce.pedido.configs;

import com.ecommerce.pedido.models.*;
import com.ecommerce.pedido.models.enums.*;
import com.ecommerce.pedido.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private RegiaoEntregaRepository regiaoEntregaRepository;

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private ComandaRepository comandaRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Iniciando carga de dados de teste...");

        // 1. CRIAR USUÁRIOS
        Usuario cliente = criarUsuario("João Cliente", "cliente@email.com", "123456", "11999999999", Role.CLIENTE);
        Usuario dono1 = criarUsuario("Maria Restaurante", "dono1@email.com", "123456", "11888888888", Role.DONO_RESTAURANTE);
        Usuario dono2 = criarUsuario("Carlos Restaurante", "dono2@email.com", "123456", "11777777777", Role.DONO_RESTAURANTE);
        Usuario garcom1 = criarUsuario("Pedro Garçom", "garcom@email.com", "123456", "11666666666", Role.GARCOM);

        // 2. CRIAR RESTAURANTES
        Restaurante restaurante1 = criarRestaurante(
                "Sabor Brasileiro",
                "Rua das Flores, 123 - São Paulo",
                "1133334444",
                "12.345.678/0001-90",
                "Comida Brasileira",
                "08:00-22:00",
                "https://exemplo.com/restaurante1.jpg",
                dono1
        );

        Restaurante restaurante2 = criarRestaurante(
                "Pizza Italiana",
                "Av. Italia, 456 - São Paulo",
                "1155556666",
                "98.765.432/0001-10",
                "Pizza",
                "18:00-23:00",
                "https://exemplo.com/restaurante2.jpg",
                dono2
        );

        // 3. CRIAR REGIÕES DE ENTREGA
        criarRegiaoEntrega("Centro", new BigDecimal("8.50"), restaurante1);
        criarRegiaoEntrega("Zona Sul", new BigDecimal("12.00"), restaurante1);
        criarRegiaoEntrega("Zona Norte", new BigDecimal("15.00"), restaurante1);
        criarRegiaoEntrega("Centro", new BigDecimal("7.00"), restaurante2);
        criarRegiaoEntrega("Zona Leste", new BigDecimal("10.00"), restaurante2);

        // 4. CRIAR PRODUTOS
        Produto feijoada = criarProduto(
                "Feijoada Completa",
                "Feijoada tradicional com todas as accompanhamentos",
                new BigDecimal("35.90"),
                "Pratos Principais",
                "https://exemplo.com/feijoada.jpg",
                restaurante1
        );

        Produto pizzaMargherita = criarProduto(
                "Pizza Margherita",
                "Pizza tradicional com molho de tomate, mussarela e manjericão",
                new BigDecimal("48.50"),
                "Pizzas",
                "https://exemplo.com/pizza.jpg",
                restaurante2
        );

        Produto refrigerante = criarProduto(
                "Refrigerante 2L",
                "Refrigerante diversos sabores",
                new BigDecimal("12.00"),
                "Bebidas",
                "https://exemplo.com/refri.jpg",
                restaurante1
        );
        refrigerante.setPrecoMeia(new BigDecimal("7.00"));
        produtoRepository.save(refrigerante);

        // 5. CRIAR PEDIDO
        Pedido pedido = criarPedido(
                "PED-001",
                "Entregar sem campainha",
                "Rua Teste, 123 - Apt 45",
                StatusPedido.CONFIRMADO,
                new BigDecimal("47.90"),
                new BigDecimal("5.00"),
                new BigDecimal("52.90"),
                "João Convidado",
                "11977776666",
                "convidado@email.com",
                cliente,
                restaurante1
        );

        // 6. CRIAR ITENS DO PEDIDO
        ItemPedido item1 = criarItemPedido(1, new BigDecimal("35.90"), pedido, feijoada);
        ItemPedido item2 = criarItemPedido(1, new BigDecimal("12.00"), pedido, refrigerante);

        // 7. CRIAR PAGAMENTO
        Pagamento pagamento = criarPagamento(
                new BigDecimal("52.90"),
                LocalDateTime.now().minusMinutes(30),
                FormaPagamento.CARTAO_DE_CREDITO,
                StatusPagamento.APROVADO,
                pedido
        );

        // Associar pagamento ao pedido
        pedido.setPagamento(pagamento);
        pedidoRepository.save(pedido);

        // 8. CRIAR MESA E COMANDAS DE EXEMPLO
        Mesa mesa1 = criarMesa("João", restaurante1);
        criarComanda("João", mesa1, garcom1, List.of(
                criarItemComanda(1, feijoada, false),
                criarItemComanda(2, refrigerante, false)
        ));
        criarComanda("Maria", mesa1, garcom1, List.of(
                criarItemComanda(1, feijoada, false),
                criarItemComanda(1, refrigerante, true)
        ));

        System.out.println("✅ Dados de teste criados com sucesso!");
        System.out.println("📧 Usuários para login:");
        System.out.println("   👤 Cliente: cliente@email.com / 123456");
        System.out.println("   🏪 Dono: dono@email.com / 123456");
        System.out.println("🍽️  Restaurantes criados: " + restaurante1.getNome() + ", " + restaurante2.getNome());
        System.out.println("📦 Pedido criado: " + pedido.getCodigoPedido());
    }

    // MÉTODOS AUXILIARES
    private Usuario criarUsuario(String nome, String email, String senha, String telefone, Role tipo) {
        if (usuarioRepository.findByEmail(email).isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setEmail(email);
            usuario.setSenha(passwordEncoder.encode(senha));
            usuario.setTelefone(telefone);
            usuario.setTipo(tipo);
            return usuarioRepository.save(usuario);
        }
        return usuarioRepository.findByEmail(email).get();
    }

    private Restaurante criarRestaurante(String nome, String endereco, String telefone, String cnpj,
                                         String tipoCozinha, String horarioFuncionamento, String imageUrl, Usuario dono) {
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(nome);
        restaurante.setEndereco(endereco);
        restaurante.setTelefone(telefone);
        restaurante.setCnpj(cnpj);
        restaurante.setTipoCozinha(tipoCozinha);
        restaurante.setHorarioFuncionamento(horarioFuncionamento);
        restaurante.setImageUrl(imageUrl);
        restaurante.setUsuario(dono);
        return restauranteRepository.save(restaurante);
    }

    private Produto criarProduto(String nome, String descricao, BigDecimal preco,
                                 String categoria, String imageUrl, Restaurante restaurante) {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setDescricao(descricao);
        produto.setPreco(preco);
        produto.setCategoria(categoria);
        produto.setImageUrl(imageUrl);
        produto.setRestaurante(restaurante);
        return produtoRepository.save(produto);
    }

    private Pedido criarPedido(String codigoPedido, String observacoes, String enderecoEntrega,
                               StatusPedido status, BigDecimal subtotal, BigDecimal taxaEntrega,
                               BigDecimal valorTotal, String nomeConvidado, String telefoneConvidado,
                               String emailConvidado, Usuario usuario, Restaurante restaurante) {
        Pedido pedido = new Pedido();
        pedido.setCodigoPedido(codigoPedido);
        pedido.setDataDoPedido(LocalDateTime.now().minusHours(1));
        pedido.setObservacoes(observacoes);
        pedido.setEnderecoDeEntrega(enderecoEntrega);
        pedido.setStatus(status);
        pedido.setSubtotal(subtotal);
        pedido.setTaxaEntrega(taxaEntrega);
        pedido.setValorTotal(valorTotal);
        pedido.setNomeUsuarioConvidado(nomeConvidado);
        pedido.setTelefoneUsuarioConvidado(telefoneConvidado);
        pedido.setEmailUsuarioConvidado(emailConvidado);
        pedido.setUsuario(usuario);
        pedido.setRestaurante(restaurante);
        return pedidoRepository.save(pedido);
    }

    private ItemPedido criarItemPedido(Integer quantidade, BigDecimal precoUnitario, Pedido pedido, Produto produto) {
        ItemPedido item = new ItemPedido();
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(precoUnitario);
        item.setPedido(pedido);
        item.setProduto(produto);
        return itemPedidoRepository.save(item);
    }

    private RegiaoEntrega criarRegiaoEntrega(String nome, BigDecimal valorFrete, Restaurante restaurante) {
        RegiaoEntrega regiao = new RegiaoEntrega();
        regiao.setNome(nome);
        regiao.setValorFrete(valorFrete);
        regiao.setRestaurante(restaurante);
        return regiaoEntregaRepository.save(regiao);
    }

    private Mesa criarMesa(String nomeCliente, Restaurante restaurante) {
        Mesa mesa = new Mesa();
        mesa.setNomeCliente(nomeCliente);
        mesa.setStatus(StatusMesa.LIVRE);
        mesa.setDataAbertura(LocalDateTime.now());
        mesa.setRestaurante(restaurante);
        return mesaRepository.save(mesa);
    }

    private void criarComanda(String clienteNome, Mesa mesa, Usuario garcom, List<ComandaItem> itens) {
        Comanda comanda = new Comanda();
        comanda.setMesa(mesa);
        comanda.setGarcom(garcom);
        comanda.setClienteNome(clienteNome);
        comanda.setStatus(StatusComanda.ABERTA);
        comanda.setDataAbertura(LocalDateTime.now());

        BigDecimal valorTotal = BigDecimal.ZERO;
        for (ComandaItem item : itens) {
            item.setComanda(comanda);
            valorTotal = valorTotal.add(item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())));
        }
        comanda.setItens(itens);
        comanda.setValorTotal(valorTotal);
        comanda.setRateios(new ArrayList<>());

        mesa.setStatus(StatusMesa.OCUPADA);
        mesaRepository.save(mesa);
        comandaRepository.save(comanda);
    }

    private ComandaItem criarItemComanda(Integer quantidade, Produto produto, boolean compartilhado) {
        ComandaItem item = new ComandaItem();
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(produto.getPreco());
        item.setProduto(produto);
        item.setCompartilhado(compartilhado);
        return item;
    }

    private Pagamento criarPagamento(BigDecimal valorTotal, LocalDateTime dataPagamento,
                                     FormaPagamento formaPagamento, StatusPagamento status, Pedido pedido) {
        Pagamento pagamento = new Pagamento();
        pagamento.setValorTotal(valorTotal);
        pagamento.setDataDoPagamento(dataPagamento);
        pagamento.setFormaDePagamento(formaPagamento);
        pagamento.setStatus(status);
        pagamento.setPedido(pedido);
        return pagamentoRepository.save(pagamento);
    }
}