// Configuração da API - usando verificação para evitar redeclaração
var pagamentosApp = {} || {}

// Inicializar apenas se ainda não estiver definido
if (!pagamentosApp.initialized) {
  pagamentosApp.initialized = true
  pagamentosApp.API_BASE_URL = "http://localhost:8080"
  pagamentosApp.API_ENDPOINTS = {
    pagamentos: pagamentosApp.API_BASE_URL + "/api/pagamentos",
    multas: pagamentosApp.API_BASE_URL + "/api/multas",
  }

  // Função para verificar se a API está disponível
  pagamentosApp.checkApiConnection = () => {
    // Usar um endpoint que sabemos que existe, com método GET
    fetch(pagamentosApp.API_ENDPOINTS.pagamentos, {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`API respondeu com status ${response.status}`)
        }
        console.log("✅ Conexão com API estabelecida")
        showAlert("Conexão com API estabelecida com sucesso!", "success")
      })
      .catch((error) => {
        console.warn("⚠️ API não está disponível:", error.message)
        showAlert(`Servidor não está disponível ou endpoint incorreto: ${error.message}`, "warning")
      })
  }

  // Inicialização
  document.addEventListener("DOMContentLoaded", () => {
    // Verificar conexão com API
    pagamentosApp.checkApiConnection()

    // Definir a data atual no campo de data
    const dataField = document.getElementById("dataPagamento")
    if (dataField) {
      dataField.valueAsDate = new Date()
    }

    // Adicionar event listener para o formulário
    const form = document.getElementById("pagamentoForm")
    if (form) {
      form.addEventListener("submit", handleFormSubmit)
    }

    // Carregar lista inicial de pagamentos
    listarTodosPagamentos()
  })
}

// Função para buscar multa por ID
function buscarMulta() {
  const multaId = document.getElementById("multaId").value

  if (!multaId) {
    showAlert("Por favor, informe o ID da multa", "error")
    return
  }

  fetch(`${pagamentosApp.API_ENDPOINTS.multas}/${multaId}`, {
    method: "GET",
    headers: {
      Accept: "application/json",
    },
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Multa não encontrada")
      }
      return response.json()
    })
    .then((data) => {
      // Verificar se a resposta está dentro de um objeto data
      const multaData = data.data || data

      // Preencher o campo de cliente
      document.getElementById("clienteId").value = multaData.clienteId

      // Preencher o valor da multa
      document.getElementById("valorPago").value = multaData.valor

      // Exibir informações da multa
      exibirInfoMulta(multaData)

      showAlert("Multa encontrada! Dados preenchidos automaticamente.", "success")
    })
    .catch((error) => {
      showAlert(`Erro ao buscar multa: ${error.message}`, "error")
    })
}

// Função para exibir informações da multa
function exibirInfoMulta(multa) {
  const infoMulta = document.getElementById("infoMulta")
  const dadosMulta = document.getElementById("dadosMulta")

  dadosMulta.innerHTML = `
        <div class="info-item">
            <strong><i class="fas fa-hashtag"></i> ID da Multa:</strong>
            <span>${multa.id}</span>
        </div>
        <div class="info-item">
            <strong><i class="fas fa-user"></i> Cliente:</strong>
            <span>${multa.clienteId} - ${multa.nomeCliente || "Nome não disponível"}</span>
        </div>
        <div class="info-item">
            <strong><i class="fas fa-dollar-sign"></i> Valor:</strong>
            <span>R$ ${multa.valor.toFixed(2)}</span>
        </div>
        <div class="info-item">
            <strong><i class="fas fa-flag"></i> Status:</strong>
            <span class="status status-${multa.status ? multa.status.toLowerCase() : "pendente"}">${multa.status || "PENDENTE"}</span>
        </div>
        <div class="info-item">
            <strong><i class="fas fa-calendar"></i> Data da Multa:</strong>
            <span>${formatarData(multa.dataMulta)}</span>
        </div>
    `

  infoMulta.style.display = "block"
}

// Função para lidar com o envio do formulário
function handleFormSubmit(event) {
  event.preventDefault()

  const formData = {
    multaId: Number.parseInt(document.getElementById("multaId").value),
    clienteId: Number.parseInt(document.getElementById("clienteId").value),
    valor: Number.parseFloat(document.getElementById("valorPago").value), // Mudou de valorPago para valor
    dataPagamento: document.getElementById("dataPagamento").value,
    formaPagamento: document.getElementById("metodoPagamento").value, // Mudou de metodoPagamento para formaPagamento
    status: document.getElementById("status").value,
    observacoes: document.getElementById("observacoes").value,
  }

  const pagamentoId = document.getElementById("pagamentoForm").dataset.id

  if (pagamentoId) {
    // Atualizar pagamento existente
    atualizarPagamento(pagamentoId, formData)
  } else {
    // Criar novo pagamento
    criarPagamento(formData)
  }
}

// Função para criar um novo pagamento
function criarPagamento(pagamento) {
  fetch(pagamentosApp.API_ENDPOINTS.pagamentos, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
    },
    body: JSON.stringify(pagamento),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`Erro ao registrar pagamento: ${response.status}`)
      }
      return response.json()
    })
    .then((data) => {
      showAlert("Pagamento registrado com sucesso!", "success")
      limparFormulario()
      listarTodosPagamentos()
    })
    .catch((error) => {
      showAlert(`Erro: ${error.message}`, "error")
    })
}

// Função para atualizar um pagamento existente
function atualizarPagamento(id, pagamento) {
  fetch(`${pagamentosApp.API_ENDPOINTS.pagamentos}/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
    },
    body: JSON.stringify(pagamento),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`Erro ao atualizar pagamento: ${response.status}`)
      }
      return response.json()
    })
    .then((data) => {
      showAlert("Pagamento atualizado com sucesso!", "success")
      limparFormulario()
      listarTodosPagamentos()
    })
    .catch((error) => {
      showAlert(`Erro: ${error.message}`, "error")
    })
}

// Função para listar todos os pagamentos
function listarTodosPagamentos() {
  fetch(pagamentosApp.API_ENDPOINTS.pagamentos, {
    method: "GET",
    headers: {
      Accept: "application/json",
    },
  })
    .then((response) => {
      if (!response.ok) {
        if (response.status === 404) {
          throw new Error("Endpoint de pagamentos não encontrado. Verifique se a API está rodando.")
        }
        throw new Error(`Erro HTTP: ${response.status}`)
      }
      return response.json()
    })
    .then((data) => {
      // Verificar se data é um array ou tem propriedade data
      const pagamentos = Array.isArray(data) ? data : data.data || []
      exibirListaPagamentos(pagamentos, "Todos os Pagamentos")
    })
    .catch((error) => {
      console.error("Erro ao listar pagamentos:", error)
      showAlert(`Erro ao carregar pagamentos: ${error.message}`, "error")

      // Exibir tabela vazia em caso de erro
      const listaPagamentos = document.getElementById("listaPagamentos")
      const tabela = document.getElementById("tabelaPagamentos")
      if (listaPagamentos && tabela) {
        const tbody = tabela.getElementsByTagName("tbody")[0]
        tbody.innerHTML =
          '<tr><td colspan="8" class="text-center">Erro ao carregar dados. Verifique se o servidor está rodando.</td></tr>'
        listaPagamentos.style.display = "block"
      }
    })
}

// Função para listar pagamentos processados
function listarPagamentosProcessados() {
  fetch(`${pagamentosApp.API_ENDPOINTS.pagamentos}/status/PROCESSADO`, {
    method: "GET",
    headers: {
      Accept: "application/json",
    },
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`Erro ao buscar pagamentos processados: ${response.status}`)
      }
      return response.json()
    })
    .then((data) => {
      const pagamentos = Array.isArray(data) ? data : data.data || []
      exibirListaPagamentos(pagamentos, "Pagamentos Processados")
    })
    .catch((error) => {
      showAlert(`Erro: ${error.message}`, "error")
    })
}

// Função para listar pagamentos pendentes
function listarPagamentosPendentes() {
  fetch(`${pagamentosApp.API_ENDPOINTS.pagamentos}/status/PENDENTE`, {
    method: "GET",
    headers: {
      Accept: "application/json",
    },
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`Erro ao buscar pagamentos pendentes: ${response.status}`)
      }
      return response.json()
    })
    .then((data) => {
      const pagamentos = Array.isArray(data) ? data : data.data || []
      exibirListaPagamentos(pagamentos, "Pagamentos Pendentes")
    })
    .catch((error) => {
      showAlert(`Erro: ${error.message}`, "error")
    })
}

// Função para buscar pagamento por ID
function buscarPagamento() {
  const pagamentoId = document.getElementById("buscarPagamentoId").value

  if (!pagamentoId) {
    showAlert("Por favor, informe o ID do pagamento", "error")
    return
  }

  fetch(`${pagamentosApp.API_ENDPOINTS.pagamentos}/${pagamentoId}`, {
    method: "GET",
    headers: {
      Accept: "application/json",
    },
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Pagamento não encontrado")
      }
      return response.json()
    })
    .then((data) => {
      const pagamento = data.data || data
      exibirDetalhesPagamento(pagamento)
    })
    .catch((error) => {
      showAlert(`Erro: ${error.message}`, "error")
    })
}

// Função para exibir lista de pagamentos
function exibirListaPagamentos(pagamentos, titulo) {
  const listaPagamentos = document.getElementById("listaPagamentos")

  // Verificar se o elemento existe
  if (!listaPagamentos) {
    console.error("Elemento 'listaPagamentos' não encontrado no DOM")
    return
  }

  const tabelaElement = document.getElementById("tabelaPagamentos")
  if (!tabelaElement) {
    console.error("Elemento 'tabelaPagamentos' não encontrado no DOM")
    return
  }

  const tbody = tabelaElement.getElementsByTagName("tbody")[0]
  if (!tbody) {
    console.error("Elemento tbody não encontrado na tabela")
    return
  }

  // Atualizar título se fornecido
  if (titulo) {
    const h2Element = listaPagamentos.querySelector("h2")
    if (h2Element) {
      h2Element.innerHTML = `<i class="fas fa-list"></i> ${titulo}`
    }
  }

  tbody.innerHTML = ""

  if (!pagamentos || pagamentos.length === 0) {
    tbody.innerHTML = '<tr><td colspan="8" class="text-center">Nenhum pagamento encontrado</td></tr>'
  } else {
    pagamentos.forEach((pagamento) => {
      const row = tbody.insertRow()

      row.innerHTML = `
                <td>${pagamento.id}</td>
                <td>${pagamento.multaId || "N/A"}</td>
                <td>${pagamento.clienteId}</td>
                <td>R$ ${pagamento.valorPago ? pagamento.valorPago.toFixed(2) : "0,00"}</td>
                <td>${formatarData(pagamento.dataPagamento)}</td>
                <td>${formatarMetodoPagamento(pagamento.metodoPagamento || pagamento.formaPagamento)}</td>
                <td><span class="status status-${pagamento.status ? pagamento.status.toLowerCase() : "pendente"}">${pagamento.status || "PENDENTE"}</span></td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="editarPagamento(${pagamento.id})">
                        <i class="fas fa-edit"></i> Editar
                    </button>
                    <button class="btn btn-danger btn-sm" onclick="excluirPagamento(${pagamento.id})">
                        <i class="fas fa-trash"></i> Excluir
                    </button>
                </td>
            `
    })
  }

  listaPagamentos.style.display = "block"
  const resultadoBusca = document.getElementById("resultadoBusca")
  if (resultadoBusca) {
    resultadoBusca.style.display = "none"
  }
}

// Função para exibir detalhes de um pagamento
function exibirDetalhesPagamento(pagamento) {
  const resultadoBusca = document.getElementById("resultadoBusca")
  const dadosPagamento = document.getElementById("dadosPagamento")

  dadosPagamento.innerHTML = `
        <div class="detail-item">
            <h4><i class="fas fa-hashtag"></i> ID do Pagamento</h4>
            <p>${pagamento.id}</p>
        </div>
        <div class="detail-item">
            <h4><i class="fas fa-exclamation-triangle"></i> Multa</h4>
            <p>${pagamento.multaId}</p>
        </div>
        <div class="detail-item">
            <h4><i class="fas fa-user"></i> Cliente</h4>
            <p>${pagamento.clienteId}</p>
        </div>
        <div class="detail-item">
            <h4><i class="fas fa-dollar-sign"></i> Valor Pago</h4>
            <p>R$ ${pagamento.valorPago.toFixed(2)}</p>
        </div>
        <div class="detail-item">
            <h4><i class="fas fa-calendar"></i> Data do Pagamento</h4>
            <p>${formatarData(pagamento.dataPagamento)}</p>
        </div>
        <div class="detail-item">
            <h4><i class="fas fa-credit-card"></i> Método de Pagamento</h4>
            <p>${formatarMetodoPagamento(pagamento.metodoPagamento)}</p>
        </div>
        <div class="detail-item">
            <h4><i class="fas fa-flag"></i> Status</h4>
            <p><span class="status status-${pagamento.status ? pagamento.status.toLowerCase() : "pendente"}">${pagamento.status || "PENDENTE"}</span></p>
        </div>
        <div class="detail-item">
            <h4><i class="fas fa-comment"></i> Observações</h4>
            <p>${pagamento.observacoes || "Nenhuma observação"}</p>
        </div>
    `

  resultadoBusca.style.display = "block"
  document.getElementById("listaPagamentos").style.display = "none"

  // Adicionar botões de ação
  dadosPagamento.innerHTML += `
        <div class="detail-item">
            <h4><i class="fas fa-cogs"></i> Ações</h4>
            <div class="action-buttons">
                <button class="btn btn-secondary" onclick="editarPagamento(${pagamento.id})">
                    <i class="fas fa-edit"></i> Editar
                </button>
                <button class="btn btn-danger" onclick="excluirPagamento(${pagamento.id})">
                    <i class="fas fa-trash"></i> Excluir
                </button>
                <button class="btn btn-primary" onclick="voltarParaLista()">
                    <i class="fas fa-arrow-left"></i> Voltar
                </button>
            </div>
        </div>
    `
}

// Função para editar um pagamento
function editarPagamento(id) {
  fetch(`${pagamentosApp.API_ENDPOINTS.pagamentos}/${id}`, {
    method: "GET",
    headers: {
      Accept: "application/json",
    },
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Pagamento não encontrado")
      }
      return response.json()
    })
    .then((data) => {
      const pagamento = data.data || data

      // Preencher o formulário com os dados do pagamento
      document.getElementById("multaId").value = pagamento.multaId
      document.getElementById("clienteId").value = pagamento.clienteId
      document.getElementById("valorPago").value = pagamento.valorPago
      document.getElementById("dataPagamento").value = pagamento.dataPagamento.split("T")[0]
      document.getElementById("metodoPagamento").value = pagamento.metodoPagamento
      document.getElementById("status").value = pagamento.status
      document.getElementById("observacoes").value = pagamento.observacoes || ""

      // Marcar o formulário para atualização
      document.getElementById("pagamentoForm").dataset.id = id

      // Atualizar o botão de submit
      document.getElementById("btnSubmit").innerHTML = '<i class="fas fa-save"></i> Atualizar Pagamento'
      document.getElementById("btnCancelar").style.display = "inline-block"

      // Rolar para o topo
      window.scrollTo(0, 0)

      showAlert("Editando pagamento #" + id, "info")
    })
    .catch((error) => {
      showAlert(`Erro: ${error.message}`, "error")
    })
}

// Função para excluir um pagamento
function excluirPagamento(id) {
  if (confirm("Tem certeza que deseja excluir este pagamento?")) {
    fetch(`${pagamentosApp.API_ENDPOINTS.pagamentos}/${id}`, {
      method: "DELETE",
      headers: {
        Accept: "application/json",
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`Erro ao excluir pagamento: ${response.status}`)
        }
        showAlert("Pagamento excluído com sucesso!", "success")
        listarTodosPagamentos()
        document.getElementById("resultadoBusca").style.display = "none"
      })
      .catch((error) => {
        showAlert(`Erro: ${error.message}`, "error")
      })
  }
}

// Função para limpar o formulário
function limparFormulario() {
  document.getElementById("pagamentoForm").reset()
  document.getElementById("pagamentoForm").removeAttribute("data-id")
  document.getElementById("btnSubmit").innerHTML = '<i class="fas fa-save"></i> Registrar Pagamento'
  document.getElementById("btnCancelar").style.display = "none"
  document.getElementById("infoMulta").style.display = "none"
  document.getElementById("dataPagamento").valueAsDate = new Date()
}

// Função para voltar para a lista
function voltarParaLista() {
  document.getElementById("resultadoBusca").style.display = "none"
  listarTodosPagamentos()
}

// Função para formatar data
function formatarData(dataString) {
  if (!dataString) return "Data não disponível"

  const data = new Date(dataString)
  return data.toLocaleDateString("pt-BR")
}

// Função para formatar método de pagamento
function formatarMetodoPagamento(metodo) {
  const metodos = {
    DINHEIRO: "Dinheiro",
    CARTAO_CREDITO: "Cartão de Crédito",
    CARTAO_DEBITO: "Cartão de Débito",
    PIX: "PIX",
    TRANSFERENCIA: "Transferência Bancária",
  }

  return metodos[metodo] || metodo || "Não informado"
}

// Função para exibir alertas
function showAlert(message, type) {
  // Verificar se o container de alertas existe, se não, criar um
  let alertContainer = document.getElementById("alertContainer")

  if (!alertContainer) {
    // Criar o container se não existir
    alertContainer = document.createElement("div")
    alertContainer.id = "alertContainer"
    alertContainer.className = "alert-container"
    alertContainer.style.cssText = `
      position: fixed;
      top: 20px;
      right: 20px;
      z-index: 9999;
      max-width: 400px;
    `
    document.body.appendChild(alertContainer)
  }

  const alertDiv = document.createElement("div")
  alertDiv.className = `alert alert-${type}`
  alertDiv.style.cssText = `
    padding: 12px 16px;
    margin-bottom: 10px;
    border-radius: 4px;
    color: white;
    font-weight: 500;
    box-shadow: 0 2px 8px rgba(0,0,0,0.15);
    animation: slideIn 0.3s ease-out;
  `

  // Definir cores baseadas no tipo
  switch (type) {
    case "success":
      alertDiv.style.backgroundColor = "#28a745"
      break
    case "error":
      alertDiv.style.backgroundColor = "#dc3545"
      break
    case "info":
      alertDiv.style.backgroundColor = "#17a2b8"
      break
    case "warning":
      alertDiv.style.backgroundColor = "#ffc107"
      alertDiv.style.color = "#212529"
      break
    default:
      alertDiv.style.backgroundColor = "#6c757d"
  }

  // Adicionar ícone baseado no tipo
  let icon = ""
  switch (type) {
    case "success":
      icon = '<i class="fas fa-check-circle"></i> '
      break
    case "error":
      icon = '<i class="fas fa-exclamation-circle"></i> '
      break
    case "info":
      icon = '<i class="fas fa-info-circle"></i> '
      break
    case "warning":
      icon = '<i class="fas fa-exclamation-triangle"></i> '
      break
    default:
      icon = ""
  }

  alertDiv.innerHTML = icon + message

  alertContainer.appendChild(alertDiv)

  // Remover o alerta após 5 segundos
  setTimeout(() => {
    if (alertDiv.parentNode) {
      alertDiv.remove()
    }
  }, 5000)

  // Adicionar CSS de animação se não existir
  if (!document.getElementById("alertAnimationCSS")) {
    const style = document.createElement("style")
    style.id = "alertAnimationCSS"
    style.textContent = `
      @keyframes slideIn {
        from {
          transform: translateX(100%);
          opacity: 0;
        }
        to {
          transform: translateX(0);
          opacity: 1;
        }
      }
    `
    document.head.appendChild(style)
  }
}
