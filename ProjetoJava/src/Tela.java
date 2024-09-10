
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

public class Tela extends javax.swing.JFrame {
    
    //cria o modelo da tabela fora do contrutor pois será usado nos métodos
    DefaultTableModel model = new DefaultTableModel();
    
    public Tela() {
        //inicia os componentes, determina o nome da janela e chama o meotodo para configurar a tabela
        initComponents();
        this.setTitle("Sistema de Trabalho");
        this.setLocationRelativeTo(null);
        adicionarItensComboBox(); 
        configurarTabela();
        
       
//      impões restrições de teclas para os campos específicos
        txtNome.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e){
                //faz leitura de caracter por caracter
                char c = e.getKeyChar();

                if (!Character.isLetter(c) && !Character.isWhitespace(c)) {
                    e.consume(); //não é uma letra ou espaço em branco
                }
            }
            //evento para pular automaticamente para o próximo campo quando "enter" for pressionado.
            public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                txtValorUnitario.requestFocus();
            }
        }
        });

        txtValorUnitario.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                // faz leitura de caracter por caracter
                char c = e.getKeyChar();

                // Verifica se o caractere é um dígito ou uma vírgula
                if (!Character.isDigit(c) && c != ',') {
                    e.consume(); // não é um dígito nem uma vírgula
                } else if (c == ',' && txtValorUnitario.getText().contains(",")) {
                    e.consume(); // já contém uma vírgula
                }
            }
            
            public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                txtQuantidade.requestFocus();
            }
        }
        });
        
        txtQuantidade.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e){
                //faz leitura de caracter por caracter
                char c = e.getKeyChar();

                if (!Character.isDigit(c)) {
                    e.consume(); //não é um dígito
                }
            }
            
            public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                btnAdicionarProduto.requestFocus();
            }
        }
        });
        
        //Evento para acionar o botão apenas com "enter".
        btnAdicionarProduto.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                btnAdicionarProduto.doClick();
            }
        }
    });//fim das determinações
    
    }//fim construtor
    
    private void configurarTabela() {
        // configurando o modelo da tabela e adicionando as colunas
        model.addColumn("Quantidade");
        model.addColumn("Nome");
        model.addColumn("Valor Unitário");
        model.addColumn("Soma");

        tabelaProdutos.setModel(model);
        tabelaProdutos.setFont(new java.awt.Font("Segoe UI", 0, 14));

        // ajustando a largura das colunas
        TableColumn quantidadeColumn = tabelaProdutos.getColumnModel().getColumn(0);
        TableColumn nomeColumn = tabelaProdutos.getColumnModel().getColumn(1);
        TableColumn valorUnitarioColumn = tabelaProdutos.getColumnModel().getColumn(2);
        TableColumn somaColumn = tabelaProdutos.getColumnModel().getColumn(3);

        quantidadeColumn.setPreferredWidth(80);
        nomeColumn.setPreferredWidth(200);
        valorUnitarioColumn.setPreferredWidth(60);
        somaColumn.setPreferredWidth(100);

        jScrollPane1.setViewportView(tabelaProdutos);
    }
    
    private void calcularValorTotal() {
        double valorTotal = 0.0;
        //determina como o valor deverá aparecer no visor de valor total (em reais)
        DecimalFormat df = new DecimalFormat("#,##0.00");
        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                // converter a string formatada de volta para double
                Number number = df.parse((String) model.getValueAt(i, 3));
                 // adiciona o valor numérico convertido (em formato double) ao valorTotal.
                valorTotal += number.doubleValue();
            } catch (Exception e) {
                // em caso de erro durante a conversão, imprime o stack trace do erro.
                e.printStackTrace();
            }
        }
        txtValorTotal.setText(df.format(valorTotal));
}
    //determina a pasta onde ficarão salvos os pdfs gerados pelo programa
    private static final String PASTA_DESTINO = "C:\\Users\\legio\\OneDrive\\Área de Trabalho\\PASTA COM QUASE TUDO\\NOTAS PADARIA NASCIMENTO\\";
    
    private void gerarPDF(String empresaNome, String filePath) {
    Document document = new Document();
    try {
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();
        
        // adiciona o nome da empresa no topo
        Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph titulo = new Paragraph(empresaNome, fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        
        // adiciona espaço após o título
        document.add(new Paragraph(" "));
        
        // cria a tabela no arquivo pdf
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{2, 4, 2, 2});
        
        // adiciona os cabeçalhos da tabela
        Font fontCabecalho = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        table.addCell(new PdfPCell(new Phrase("Quantidade", fontCabecalho)));
        table.addCell(new PdfPCell(new Phrase("Nome", fontCabecalho)));
        table.addCell(new PdfPCell(new Phrase("Valor Unitário", fontCabecalho)));
        table.addCell(new PdfPCell(new Phrase("Soma", fontCabecalho)));
        
        // adiciona as linhas da tabela
        for (int i = 0; i < model.getRowCount(); i++) {
            table.addCell(model.getValueAt(i, 0).toString());
            table.addCell(model.getValueAt(i, 1).toString());
            table.addCell(model.getValueAt(i, 2).toString());
            table.addCell(model.getValueAt(i, 3).toString());
        }
        
        document.add(table);
        
        // adiciona o valor total
        document.add(new Paragraph(" "));
        Font fontTotal = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Paragraph total = new Paragraph("Valor Total: " + txtValorTotal.getText(), fontTotal);
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);
        
    } catch (DocumentException | IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Erro ao gerar o PDF: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    } finally {
        document.close();
    }
}
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        painel = new javax.swing.JPanel();
        lblNome = new javax.swing.JLabel();
        lblValorUnitario = new javax.swing.JLabel();
        lblQuantidade = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        txtQuantidade = new javax.swing.JTextField();
        txtValorUnitario = new javax.swing.JTextField();
        btnAdicionarProduto = new javax.swing.JButton();
        btnExcluirProduto = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelaProdutos = new javax.swing.JTable();
        lblValorTotal = new javax.swing.JLabel();
        txtValorTotal = new javax.swing.JTextField();
        btnGerarPDF = new javax.swing.JButton();
        cbSelecionar = new javax.swing.JComboBox<>();
        btnEditarProduto = new javax.swing.JButton();
        btnSalvarProduto = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        menuArquivo = new javax.swing.JMenu();
        jmiSair = new javax.swing.JMenuItem();
        menuAjuda = new javax.swing.JMenu();
        jmiSobre = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        painel.setBackground(new java.awt.Color(204, 204, 204));
        painel.setForeground(new java.awt.Color(204, 204, 204));

        lblNome.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblNome.setText("Nome");

        lblValorUnitario.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblValorUnitario.setText("Valor Unitário");

        lblQuantidade.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblQuantidade.setText("Quantidade");

        txtNome.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        txtQuantidade.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        txtValorUnitario.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        btnAdicionarProduto.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnAdicionarProduto.setText("Adicionar Produto");
        btnAdicionarProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarProdutoActionPerformed(evt);
            }
        });

        btnExcluirProduto.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnExcluirProduto.setText("Excluir Produto");
        btnExcluirProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirProdutoActionPerformed(evt);
            }
        });

        tabelaProdutos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tabelaProdutos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ));
        jScrollPane1.setViewportView(tabelaProdutos);

        lblValorTotal.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblValorTotal.setText("Valor Total");

        txtValorTotal.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        btnGerarPDF.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnGerarPDF.setText("Gerar PDF");
        btnGerarPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGerarPDFActionPerformed(evt);
            }
        });

        cbSelecionar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbSelecionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSelecionarActionPerformed(evt);
            }
        });

        btnEditarProduto.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnEditarProduto.setText("Editar");
        btnEditarProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarProdutoActionPerformed(evt);
            }
        });

        btnSalvarProduto.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnSalvarProduto.setText("Salvar");
        btnSalvarProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarProdutoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout painelLayout = new javax.swing.GroupLayout(painel);
        painel.setLayout(painelLayout);
        painelLayout.setHorizontalGroup(
            painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(painelLayout.createSequentialGroup()
                        .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblNome, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblValorUnitario, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                            .addComponent(lblQuantidade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(12, 12, 12)
                        .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtQuantidade, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                                .addComponent(txtValorUnitario, javax.swing.GroupLayout.Alignment.LEADING)))
                        .addGap(119, 119, 119)
                        .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAdicionarProduto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnExcluirProduto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(painelLayout.createSequentialGroup()
                                .addComponent(btnEditarProduto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnSalvarProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(painelLayout.createSequentialGroup()
                        .addComponent(lblValorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtValorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbSelecionar, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btnGerarPDF)))
                .addContainerGap())
        );
        painelLayout.setVerticalGroup(
            painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelLayout.createSequentialGroup()
                .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(painelLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNome)
                            .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblValorUnitario)
                            .addComponent(txtValorUnitario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblQuantidade)
                            .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(painelLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(btnAdicionarProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExcluirProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnEditarProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSalvarProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbSelecionar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(painelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblValorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtValorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnGerarPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        menuArquivo.setText("Arquivo");

        jmiSair.setText("Sair");
        jmiSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiSairActionPerformed(evt);
            }
        });
        menuArquivo.add(jmiSair);

        menuBar.add(menuArquivo);

        menuAjuda.setText("Ajuda");

        jmiSobre.setText("Sobre");
        jmiSobre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiSobreActionPerformed(evt);
            }
        });
        menuAjuda.add(jmiSobre);

        menuBar.add(menuAjuda);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(painel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(painel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGerarPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGerarPDFActionPerformed
        //quando clicar no botão será solicitado nome da empresa e nome do arquivo
        String empresaNome = JOptionPane.showInputDialog(this, "Digite o nome da empresa:", "Nome da Empresa", JOptionPane.INFORMATION_MESSAGE);
    if (empresaNome != null && !empresaNome.trim().isEmpty()) {
        String nomeArquivo = JOptionPane.showInputDialog(this, "Digite o nome do arquivo (sem extensão):", "Nome do Arquivo", JOptionPane.INFORMATION_MESSAGE);
        if (nomeArquivo != null && !nomeArquivo.trim().isEmpty()) {
            File fileToSave = new File(PASTA_DESTINO + nomeArquivo + ".pdf");
            gerarPDF(empresaNome, fileToSave.getAbsolutePath());
        } else {
            JOptionPane.showMessageDialog(this, "Nome do arquivo não pode ser vazio.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(this, "Nome da empresa não pode ser vazio.", "Erro", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnGerarPDFActionPerformed

    private void btnAdicionarProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarProdutoActionPerformed
    try {   
            // verifica se todos os campos estão preenchidos
            if (txtNome.getText().isEmpty() || txtQuantidade.getText().isEmpty() || txtValorUnitario.getText().isEmpty()) {
                throw new Exception("Por favor, preencha todos os campos.");
            }
            
            //converte quantidade e valor unitário para int e double
            String nome = txtNome.getText().trim();
            int quantidade = Integer.parseInt(txtQuantidade.getText().trim());
            double valorUnitario = Double.parseDouble(txtValorUnitario.getText().trim().replace(",", "."));

            // calcula o total para o item atual
            double soma = quantidade * valorUnitario;

            // formatar os valores
            DecimalFormat df = new DecimalFormat("#,##0.00");
            String valorUnitarioFormatado = df.format(valorUnitario);
            String somaFormatada = df.format(soma);

            // adicionar os valores formatados na tabela
            model.addRow(new Object[]{quantidade, nome, valorUnitarioFormatado, somaFormatada});

            // limpa os campos e foca no campo nome
            txtNome.setText("");
            txtQuantidade.setText("");
            txtValorUnitario.setText("");
            txtNome.requestFocus();
            
            calcularValorTotal();

        } catch (NumberFormatException e) {
            // trata erro de formato inválido
            JOptionPane.showMessageDialog(this, "Quantidade ou Valor Unitário inválidos.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            // trata outros erros
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAdicionarProdutoActionPerformed

    private void btnExcluirProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirProdutoActionPerformed
        //captura o campo selecionado e o remove, também recalculando o valor total
        int selectedRow = tabelaProdutos.getSelectedRow();
        if (selectedRow != -1) {
            model.removeRow(selectedRow);
            calcularValorTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um produto para excluir.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnExcluirProdutoActionPerformed

    private void jmiSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiSairActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jmiSairActionPerformed

    private void jmiSobreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiSobreActionPerformed
        JOptionPane.showMessageDialog(this, 
                "Criador: Otavio\nData: 2024",
                "Sobre", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jmiSobreActionPerformed

    
      // Método para adicionar itens à JComboBox
    private void adicionarItensComboBox() {
        cbSelecionar.removeAllItems(); // Remove todos os itens existentes
        String[] items = {"Selecione um modelo","Turatti", "Zeca", "Giongo"};
        for (String item : items) {
            cbSelecionar.addItem(item);
        }
    }
    
    private void cbSelecionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSelecionarActionPerformed
        // Obtenha o item selecionado
        String itemSelecionado = (String) cbSelecionar.getSelectedItem();

        // Verifique se um item válido foi selecionado
        if (itemSelecionado != null && !itemSelecionado.equals("Selecione um modelo")) {
            // Defina os valores padrão baseados no item selecionado
            DefaultTableModel model = (DefaultTableModel) tabelaProdutos.getModel();

            // Limpe as linhas atuais na tabela
            model.setRowCount(0);

            // Adicione os itens baseados no padrão selecionado
            switch (itemSelecionado) {
                case "Turatti":
                    adicionarItensTuratti(model);
                    break;
                case "Zeca":
                    adicionarItensZeca(model);
                    break;
                case "Giongo":
                    adicionarItensGiongo(model);
                    break;
            }

            // Limpa a seleção da JComboBox
            cbSelecionar.setSelectedIndex(0);
        }
    }//GEN-LAST:event_cbSelecionarActionPerformed

    private void adicionarItensTuratti(DefaultTableModel model) {
        Object[][] itensTuratti = {
                {"", "Sanduíche", "3,50", ""},
                {"", "Centeio", "6,00", ""},
                {"", "Integral", "6,00", ""},
                {"", "Cachorro", "4,50", ""},
                {"", "Baguete", "4,50", ""},
                {"", "Pct Gajeta", "40,00", ""}
        };

        // Adicione cada item na tabela
        for (Object[] item : itensTuratti) {
            model.addRow(item);
        }
    }

    private void adicionarItensZeca(DefaultTableModel model) {
        Object[][] itensZeca = {
                {"", "Rosca Mista", "7,70", ""},
                {"", "Rosca Prestígio", "7,70", ""},
                {"", "Rosca Milho", "7,70", ""},
                {"", "Rosca Glaceada", "7,70", ""},
                {"", "Rosca Glaceada Côco", "7,70", ""}
        };

        // Adicione cada item na tabela
        for (Object[] item : itensZeca) {
            model.addRow(item);
        }
    }

    private void adicionarItensGiongo(DefaultTableModel model) {
        Object[][] itensGiongo = {
                {"", "Polvilho", "5,00", ""}
        };

        // Adicione cada item na tabela
        for (Object[] item : itensGiongo) {
            model.addRow(item);
        }
    }

    private void btnEditarProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarProdutoActionPerformed
        int selectedRow = tabelaProdutos.getSelectedRow();
        if (selectedRow != -1) {

            // Obter os valores da linha selecionada
            String nome = (String) model.getValueAt(selectedRow, 1);
            String valorUnitario = model.getValueAt(selectedRow, 2).toString();
            String quantidade = model.getValueAt(selectedRow, 0).toString();

            // Preencher os campos de texto com os valores obtidos
            txtNome.setText(nome);
            txtValorUnitario.setText(valorUnitario);
            txtQuantidade.setText(quantidade);
        }
    }//GEN-LAST:event_btnEditarProdutoActionPerformed

    private void btnSalvarProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarProdutoActionPerformed
        int selectedRow = tabelaProdutos.getSelectedRow();
        if (selectedRow != -1) {

            // obtém os novos valores dos campos de texto
            //converte quantidade e valor unitário para int e double
            String novoNome = txtNome.getText().trim();
            double novoValorUnitario = Double.parseDouble(txtValorUnitario.getText().trim().replace(",", "."));
            int novaQuantidade = Integer.parseInt(txtQuantidade.getText().trim());

            // calcula o total para o item atual
            double soma = novaQuantidade * novoValorUnitario;

            // formata os valores
            DecimalFormat df = new DecimalFormat("#,##0.00");
            String valorUnitarioFormatado = df.format(novoValorUnitario);
            String somaFormatada = df.format(soma);

            // atualiza os valores na linha selecionada
            model.setValueAt(novaQuantidade, selectedRow, 0);
            model.setValueAt(novoNome, selectedRow, 1);
            model.setValueAt(valorUnitarioFormatado, selectedRow, 2);
            model.setValueAt(somaFormatada, selectedRow, 3);

            // limpa os campos e foca no campo nome
            txtNome.setText("");
            txtQuantidade.setText("");
            txtValorUnitario.setText("");
            txtNome.requestFocus();

            calcularValorTotal();
        }
    }//GEN-LAST:event_btnSalvarProdutoActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Tela.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Tela.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Tela.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Tela.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
            Tela tela = new Tela();
            tela.setVisible(true);
        }
    });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionarProduto;
    private javax.swing.JButton btnEditarProduto;
    private javax.swing.JButton btnExcluirProduto;
    private javax.swing.JButton btnGerarPDF;
    private javax.swing.JButton btnSalvarProduto;
    private javax.swing.JComboBox<String> cbSelecionar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuItem jmiSair;
    private javax.swing.JMenuItem jmiSobre;
    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblQuantidade;
    private javax.swing.JLabel lblValorTotal;
    private javax.swing.JLabel lblValorUnitario;
    private javax.swing.JMenu menuAjuda;
    private javax.swing.JMenu menuArquivo;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JPanel painel;
    private javax.swing.JTable tabelaProdutos;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtQuantidade;
    private javax.swing.JTextField txtValorTotal;
    private javax.swing.JTextField txtValorUnitario;
    // End of variables declaration//GEN-END:variables
}
