// Variável para controlar edição
let multaEditando = null

// Função para carregar multas do localStorage
function carregarMultasDoStorage() {
  const multasSalvas = localStorage.getItem("multasLocais")
  if (multasSalvas) {
    return JSON.parse(multasSalvas)
  }
  // Retorna array vazio se não houver nada salvo
  return []
}

// Função para salvar multas no localStorage
function salvarMultasNoStorage(multas) {
  localStorage.setItem("multasLocais", JSON.stringify(multas))
}

// Função para obter próximo ID
function obterProximoId(multas) {
  if (multas.length === 0) return 1
  return Math.max(...multas.map((m) => m.id)) + 1
}

// Array para armazenar as multas localmente
const multasLocais = carregarMultasDoStorage()
let proximoId = obterProximoId(multasLocais)

document.addEventListener("DOMContentLoaded", () => {
  // Definir data padrão como 30 dias a partir de hoje
  const dataVencimento = new Date()
  dataVencimento.setDate(dataVencimento.getDate() + 30)
  document.getElementById("dataVencimento").value = dataVencimento.toISOString().split("T")[0]

  // Submit do formulário
  document.getElementById("multaForm").addEventListener("submit", salvarMulta)

  // Tornar funções acessíveis globalmente
  window.listarTodasMultas = listarTodasMultas
  window.buscarMulta = buscarMulta
  window.editarMulta = editarMulta
  window.excluirMulta = excluirMulta
  window.limparFormulario = limparFormulario
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
      response = await apiRequest(`http://localhost:8080/api/multas/${multaEditando}`, {
        method: "PUT",
        body: JSON.stringify(multaData),
      })
    } else {
      // Criar nova multa
      response = await apiRequest(`http://localhost:8080/api/multas`, {
        method: "POST",
        body: JSON.stringify(multaData),
      })
    }

    if (response) {
      const mensagem = multaEditando ? "Multa atualizada com sucesso!" : `Multa criada com sucesso! ID: ${response.id}`
      showAlert(mensagem, "success")
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
    const response = await apiRequest(`http://localhost:8080/api/multas`)

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
    const response = await apiRequest(`http://localhost:8080/api/multas/${id}`)

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
    const response = await apiRequest(`http://localhost:8080/api/multas/${id}`)

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
    // Usar apiRequest em vez de fetch diretamente
    const response = await apiRequest(`http://localhost:8080/api/multas/${id}`, {
      method: "DELETE",
    })

    if (response !== null) {
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
  console.log("API Request:", url, options)
  const method = options.method || "GET"

  // Listar todas as multas
  if (url.includes("/api/multas") && method === "GET" && !url.includes("/api/multas/")) {
    return multasLocais
  }

  // Buscar multa por ID
  if (url.includes("/api/multas/") && method === "GET") {
    const id = Number.parseInt(url.split("/").pop())
    return multasLocais.find((multa) => multa.id === id) || null
  }

  // Criar nova multa
  if (url.includes("/api/multas") && method === "POST") {
    const novaMulta = JSON.parse(options.body)
    novaMulta.id = proximoId++
    multasLocais.push(novaMulta)
    salvarMultasNoStorage(multasLocais) // Salvar no localStorage
    return novaMulta
  }

  // Atualizar multa
  if (url.includes("/api/multas/") && method === "PUT") {
    const id = Number.parseInt(url.split("/").pop())
    const dadosAtualizados = JSON.parse(options.body)
    const index = multasLocais.findIndex((multa) => multa.id === id)
    if (index !== -1) {
      multasLocais[index] = { ...multasLocais[index], ...dadosAtualizados }
      salvarMultasNoStorage(multasLocais) // Salvar no localStorage
      return multasLocais[index]
    }
    return null
  }

  // Deletar multa
  if (url.includes("/api/multas/") && method === "DELETE") {
    const id = Number.parseInt(url.split("/").pop())
    const index = multasLocais.findIndex((multa) => multa.id === id)
    if (index !== -1) {
      multasLocais.splice(index, 1)
      salvarMultasNoStorage(multasLocais) // Salvar no localStorage
      return {} // Retorna objeto vazio para indicar sucesso
    }
    return null // Retorna null para indicar erro
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
