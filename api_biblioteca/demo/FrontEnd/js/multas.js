let multaEditando = null

// Remove a declaração duplicada - usa a variável global do config.js

document.addEventListener("DOMContentLoaded", () => {
  // Definir data padrão como 30 dias a partir de hoje
  const dataVencimento = new Date()
  dataVencimento.setDate(dataVencimento.getDate() + 30)
  document.getElementById("dataVencimento").value = dataVencimento.toISOString().split("T")[0]

  // Submit do formulário
  document.getElementById("multaForm").addEventListener("submit", salvarMulta)
})

async function salvarMulta(event) {
  event.preventDefault()

  const formData = new FormData(event.target)
  const multaData = {
    clienteId: Number.parseInt(formData.get("clienteId")),
    emprestimoId: Number.parseInt(formData.get("emprestimoId")),
    valor: Number.parseFloat(formData.get("valor")),
    dataVencimento: formData.get("dataVencimento"),
    status: formData.get("status"),
    motivo: formData.get("motivo"),
  }

  try {
    let response
    if (multaEditando) {
      // Atualizar multa existente
      response = await apiRequest(`${API_BASE_URL}/api/multas/${multaEditando}`, {
        method: "PUT",
        body: JSON.stringify(multaData),
      })
    } else {
      // Criar nova multa
      response = await apiRequest(`${API_BASE_URL}/api/multas`, {
        method: "POST",
        body: JSON.stringify(multaData),
      })
    }

    if (response) {
      showAlert(multaEditando ? "Multa atualizada com sucesso!" : "Multa criada com sucesso!", "success")
      limparFormulario()

      // Atualizar lista se estiver visível
      if (document.getElementById("listaMultas").style.display !== "none") {
        listarTodasMultas()
      }
    }
  } catch (error) {
    console.error("Erro ao salvar multa:", error)
    showAlert("Erro ao salvar multa", "error")
  }
}

async function listarTodasMultas() {
  try {
    const response = await apiRequest(`${API_BASE_URL}/api/multas`)

    if (response && Array.isArray(response)) {
      exibirListaMultas(response, "Todas as Multas")
      document.getElementById("resultadoBusca").style.display = "none"
    } else {
      showAlert("Nenhuma multa encontrada", "info")
    }
  } catch (error) {
    console.error("Erro ao listar multas:", error)
    showAlert("Erro ao carregar lista de multas", "error")
  }
}

async function buscarMulta() {
  const id = document.getElementById("buscarMultaId").value

  if (!id) {
    showAlert("Digite o ID da multa", "error")
    return
  }

  try {
    const response = await apiRequest(`${API_BASE_URL}/api/multas/${id}`)

    if (response) {
      exibirDadosMulta(response)
      document.getElementById("listaMultas").style.display = "none"
    } else {
      showAlert("Multa não encontrada", "error")
      document.getElementById("resultadoBusca").style.display = "none"
    }
  } catch (error) {
    console.error("Erro ao buscar multa:", error)
    showAlert("Erro ao buscar multa", "error")
    document.getElementById("resultadoBusca").style.display = "none"
  }
}

function exibirListaMultas(multas, titulo) {
  const container = document.getElementById("listaMultas")
  const tabela = document.getElementById("tabelaMultas").getElementsByTagName("tbody")[0]

  // Limpar tabela
  tabela.innerHTML = ""

  // Atualizar título
  container.querySelector("h3").textContent = `⚠️ ${titulo}`

  // Preencher tabela
  multas.forEach((multa) => {
    const row = tabela.insertRow()
    const statusClass = getMultaStatusClass(multa.status)

    row.innerHTML = `
            <td>${multa.id}</td>
            <td>${multa.clienteId}</td>
            <td>${multa.emprestimoId}</td>
            <td>${formatCurrency(multa.valor)}</td>
            <td>${formatDate(multa.dataVencimento)}</td>
            <td><span class="status ${statusClass}">${multa.status}</span></td>
            <td>${multa.motivo || "-"}</td>
            <td class="actions">
                <button class="btn btn-warning btn-sm" onclick="editarMulta(${multa.id})">Editar</button>
                <button class="btn btn-danger btn-sm" onclick="excluirMulta(${multa.id})">Excluir</button>
            </td>
        `
  })

  container.style.display = "block"
}

function exibirDadosMulta(multa) {
  const container = document.getElementById("dadosMulta")

  container.innerHTML = `
        <div class="multa-info">
            <p><strong>ID:</strong> ${multa.id}</p>
            <p><strong>Cliente ID:</strong> ${multa.clienteId}</p>
            <p><strong>Empréstimo ID:</strong> ${multa.emprestimoId}</p>
            <p><strong>Valor:</strong> ${formatCurrency(multa.valor)}</p>
            <p><strong>Data de Vencimento:</strong> ${formatDate(multa.dataVencimento)}</p>
            <p><strong>Status:</strong> <span class="status ${getMultaStatusClass(multa.status)}">${multa.status}</span></p>
            <p><strong>Motivo:</strong> ${multa.motivo || "-"}</p>
            <div class="actions" style="margin-top: 1rem;">
                <button class="btn btn-warning btn-sm" onclick="editarMulta(${multa.id})">Editar</button>
                <button class="btn btn-danger btn-sm" onclick="excluirMulta(${multa.id})">Excluir</button>
            </div>
        </div>
    `

  document.getElementById("resultadoBusca").style.display = "block"
}

function editarMulta(id) {
  buscarMultaParaEdicao(id)
}

async function buscarMultaParaEdicao(id) {
  try {
    const response = await apiRequest(`${API_BASE_URL}/api/multas/${id}`)

    if (response) {
      const multa = response

      // Preencher formulário
      document.getElementById("clienteId").value = multa.clienteId
      document.getElementById("emprestimoId").value = multa.emprestimoId
      document.getElementById("valor").value = multa.valor
      document.getElementById("dataVencimento").value = multa.dataVencimento
      document.getElementById("status").value = multa.status
      document.getElementById("motivo").value = multa.motivo || ""

      // Alterar estado do formulário para edição
      multaEditando = id
      document.getElementById("btnSubmit").textContent = "Atualizar Multa"
      document.getElementById("btnCancelar").style.display = "inline-block"

      // Scroll para o formulário
      document.getElementById("multaForm").scrollIntoView({ behavior: "smooth" })
    }
  } catch (error) {
    console.error("Erro ao buscar multa para edição:", error)
    showAlert("Erro ao carregar dados da multa", "error")
  }
}

async function excluirMulta(id) {
  if (!confirm("Tem certeza que deseja excluir esta multa?")) {
    return
  }

  try {
    const response = await fetch(`${API_BASE_URL}/api/multas/${id}`, {
      method: "DELETE",
    })

    if (response.ok) {
      showAlert("Multa excluída com sucesso!", "success")

      // Atualizar lista se estiver visível
      if (document.getElementById("listaMultas").style.display !== "none") {
        listarTodasMultas()
      }

      // Limpar busca individual se for a mesma multa
      if (document.getElementById("resultadoBusca").style.display !== "none") {
        document.getElementById("resultadoBusca").style.display = "none"
      }

      document.getElementById("buscarMultaId").value = ""
    } else {
      showAlert("Erro ao excluir multa", "error")
    }
  } catch (error) {
    console.error("Erro ao excluir multa:", error)
    showAlert("Erro ao excluir multa", "error")
  }
}

function getMultaStatusClass(status) {
  switch (status) {
    case "PAGA":
      return "status-paga"
    case "PENDENTE":
      return "status-pendente"
    case "CANCELADA":
      return "status-cancelada"
    default:
      return "status-pendente"
  }
}

function limparFormulario() {
  document.getElementById("multaForm").reset()
  multaEditando = null
  document.getElementById("btnSubmit").textContent = "Criar Multa"
  document.getElementById("btnCancelar").style.display = "none"

  // Redefinir data padrão
  const dataVencimento = new Date()
  dataVencimento.setDate(dataVencimento.getDate() + 30)
  document.getElementById("dataVencimento").value = dataVencimento.toISOString().split("T")[0]
}

// Funções auxiliares (simulações, ajuste conforme necessário)
async function apiRequest(url, options = {}) {
  // Simulação de chamada à API (substitua pela lógica real)
  console.log("API Request:", url, options)
  const method = options.method || "GET"

  // Simulação de diferentes respostas
  if (url.includes("/api/multas/1") && method === "GET") {
    return {
      id: 1,
      clienteId: 123,
      emprestimoId: 456,
      valor: 50.0,
      dataVencimento: "2024-03-15",
      status: "PENDENTE",
      motivo: "Atraso na devolução",
    }
  }

  if (url.includes("/api/multas") && method === "GET" && !url.includes("/api/multas/")) {
    return [
      {
        id: 1,
        clienteId: 123,
        emprestimoId: 456,
        valor: 50.0,
        dataVencimento: "2024-03-15",
        status: "PENDENTE",
        motivo: "Atraso na devolução",
      },
      {
        id: 2,
        clienteId: 789,
        emprestimoId: 101,
        valor: 25.5,
        dataVencimento: "2024-03-20",
        status: "PAGA",
        motivo: null,
      },
    ]
  }

  if (url.includes("/api/multas") && method === "POST") {
    return {
      id: 3,
      clienteId: 112,
      emprestimoId: 131,
      valor: 100.0,
      dataVencimento: "2024-04-20",
      status: "PENDENTE",
      motivo: "Livro danificado",
    }
  }

  if (url.includes("/api/multas/1") && method === "PUT") {
    return {
      id: 1,
      clienteId: 999,
      emprestimoId: 888,
      valor: 75.0,
      dataVencimento: "2024-03-15",
      status: "PAGA",
      motivo: "Atraso na devolução",
    }
  }

  if (url.includes("/api/multas/1") && method === "DELETE") {
    return {}
  }

  return null
}

function showAlert(message, type) {
  // Simulação de alerta (substitua pela lógica real)
  alert(`${type.toUpperCase()}: ${message}`)
}

function formatCurrency(value) {
  // Simulação de formatação de moeda (substitua pela lógica real)
  return `R$ ${value.toFixed(2)}`
}

function formatDate(dateString) {
  // Simulação de formatação de data (substitua pela lógica real)
  const date = new Date(dateString)
  return date.toLocaleDateString("pt-BR")
}
