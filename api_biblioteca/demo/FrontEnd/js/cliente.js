// ===== CONFIGURAÇÃO DA API =====
const API_BASE_URL = "http://localhost:8080/api"
const API_ENDPOINTS = {
  clientes: `${API_BASE_URL}/clientes`,
  livros: `${API_BASE_URL}/livros`,
  emprestimos: `${API_BASE_URL}/emprestimos`,
  reservas: `${API_BASE_URL}/reservas`,
  multas: `${API_BASE_URL}/multa`,
  pagamentos: `${API_BASE_URL}/pagamentos`,
}

// ===== VARIÁVEIS GLOBAIS =====
let clienteEditando = null

// ===== FUNÇÕES UTILITÁRIAS =====
async function apiRequest(url, options = {}) {
  const defaultOptions = {
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
    },
  }

  const config = { ...defaultOptions, ...options }

  try {
    console.log("Fazendo requisição para:", url, config)
    const response = await fetch(url, config)
    const data = await response.json()

    console.log("Resposta da API:", { url, status: response.status, data })
    return data
  } catch (error) {
    console.error("Erro na requisição:", error)
    throw error
  }
}

function handleApiResponse(response, successMessage = null) {
  console.log("Tratando resposta:", response)

  if (response.success) {
    let message = response.message || successMessage || "Operação realizada com sucesso!"

    if (response.data && response.data.id && successMessage && successMessage.includes("cadastrado")) {
      message = `Cliente cadastrado com sucesso! ID: ${response.data.id}`
    }

    showAlert(message, "success")
    return true
  } else {
    const errorMessage = response.error?.message || response.message || "Ocorreu um erro!"
    showAlert(errorMessage, "error")

    if (response.error?.details) {
      console.error("Detalhes do erro:", response.error.details)
    }
    return false
  }
}

function showAlert(message, type = "info") {
  const existingAlerts = document.querySelectorAll(".alert")
  existingAlerts.forEach((alert) => alert.remove())

  const alert = document.createElement("div")
  alert.className = `alert alert-${type}`

  const icons = {
    success: "✅",
    error: "❌",
    warning: "⚠️",
    info: "ℹ️",
  }

  const colors = {
    success: "#059669",
    error: "#dc2626",
    warning: "#f59e0b",
    info: "#0284c7",
  }

  alert.innerHTML = `
    <span class="alert-icon">${icons[type]}</span>
    <span class="alert-message">${message}</span>
    <button class="alert-close" onclick="this.parentElement.remove()">×</button>
  `

  alert.style.cssText = `
    position: fixed;
    top: 20px;
    right: 20px;
    z-index: 10000;
    padding: 15px 20px;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    display: flex;
    align-items: center;
    gap: 10px;
    max-width: 400px;
    animation: slideIn 0.3s ease-out;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    font-size: 14px;
    color: white;
    background-color: ${colors[type]};
  `

  alert.querySelector(".alert-close").style.cssText = `
    background: none;
    border: none;
    color: white;
    font-size: 18px;
    cursor: pointer;
    padding: 0;
    margin-left: auto;
  `

  document.body.appendChild(alert)

  setTimeout(() => {
    if (alert.parentElement) {
      alert.remove()
    }
  }, 5000)
}

// ===== FUNÇÕES DE FORMATAÇÃO =====
function formatarCPF(cpf) {
  cpf = cpf.replace(/\D/g, "")
  if (cpf.length <= 11) {
    cpf = cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4")
  }
  return cpf
}

function formatarTelefone(telefone) {
  telefone = telefone.replace(/\D/g, "")
  if (telefone.length === 11) {
    telefone = telefone.replace(/(\d{2})(\d{5})(\d{4})/, "($1) $2-$3")
  } else if (telefone.length === 10) {
    telefone = telefone.replace(/(\d{2})(\d{4})(\d{4})/, "($1) $2-$3")
  }
  return telefone
}

function validarCPF(cpf) {
  cpf = cpf.replace(/[^\d]+/g, "")
  if (cpf == "") return false

  if (
    cpf.length != 11 ||
    cpf == "00000000000" ||
    cpf == "11111111111" ||
    cpf == "22222222222" ||
    cpf == "33333333333" ||
    cpf == "44444444444" ||
    cpf == "55555555555" ||
    cpf == "66666666666" ||
    cpf == "77777777777" ||
    cpf == "88888888888" ||
    cpf == "99999999999"
  )
    return false

  let add = 0
  let i
  for (i = 0; i < 9; i++) add += Number.parseInt(cpf.charAt(i)) * (10 - i)
  let rev = 11 - (add % 11)
  if (rev == 10 || rev == 11) rev = 0
  if (rev != Number.parseInt(cpf.charAt(9))) return false

  add = 0
  for (i = 0; i < 10; i++) add += Number.parseInt(cpf.charAt(i)) * (11 - i)
  rev = 11 - (add % 11)
  if (rev == 10 || rev == 11) rev = 0
  if (rev != Number.parseInt(cpf.charAt(10))) return false

  return true
}

// ===== FUNÇÕES PRINCIPAIS =====
async function salvarCliente(event) {
  event.preventDefault()
  console.log("Salvando cliente...")

  const formData = new FormData(event.target)
  const clienteData = {
    nome: formData.get("nome"),
    documento: formData.get("documento").replace(/\D/g, ""),
    email: formData.get("email"),
    telefone: formData.get("telefone").replace(/\D/g, ""),
    endereco: formData.get("endereco"),
  }

  console.log("Dados do cliente:", clienteData)

  if (!validarCPF(clienteData.documento)) {
    showAlert("CPF inválido!", "error")
    return
  }

  try {
    let response
    if (clienteEditando) {
      console.log("Atualizando cliente ID:", clienteEditando)
      response = await apiRequest(`${API_ENDPOINTS.clientes}/${clienteEditando}`, {
        method: "PUT",
        body: JSON.stringify(clienteData),
      })
    } else {
      console.log("Criando novo cliente")
      response = await apiRequest(API_ENDPOINTS.clientes, {
        method: "POST",
        body: JSON.stringify(clienteData),
      })
    }

    if (
      handleApiResponse(
        response,
        clienteEditando ? "Cliente atualizado com sucesso!" : "Cliente cadastrado com sucesso!",
      )
    ) {
      limparFormulario()
    }
  } catch (error) {
    console.error("Erro ao salvar cliente:", error)
    showAlert("Erro de conexão com o servidor", "error")
  }
}

async function buscarCliente() {
  const id = document.getElementById("buscarId").value

  if (!id) {
    showAlert("Digite o ID do cliente", "error")
    return
  }

  console.log("Buscando cliente ID:", id)

  try {
    const response = await apiRequest(`${API_ENDPOINTS.clientes}/${id}`)

    if (response.success && response.data) {
      exibirDadosCliente(response.data)
    } else {
      handleApiResponse(response)
      document.getElementById("resultadoBusca").style.display = "none"
    }
  } catch (error) {
    console.error("Erro ao buscar cliente:", error)
    showAlert("Erro de conexão com o servidor", "error")
    document.getElementById("resultadoBusca").style.display = "none"
  }
}

function exibirDadosCliente(cliente) {
  console.log("Exibindo dados do cliente:", cliente)

  const container = document.getElementById("dadosCliente")
  container.innerHTML = `
    <div class="cliente-info">
        <p><strong>ID:</strong> ${cliente.id}</p>
        <p><strong>Nome:</strong> ${cliente.nome}</p>
        <p><strong>CPF:</strong> ${formatarCPF(cliente.documento)}</p>
        <p><strong>E-mail:</strong> ${cliente.email}</p>
        <p><strong>Telefone:</strong> ${formatarTelefone(cliente.telefone)}</p>
        <p><strong>Endereço:</strong> ${cliente.endereco}</p>
        <div class="actions" style="margin-top: 1rem;">
            <button class="btn btn-warning btn-sm" onclick="editarCliente(${cliente.id})">Editar</button>
            <button class="btn btn-danger btn-sm" onclick="excluirCliente(${cliente.id})">Excluir</button>
        </div>
    </div>
  `

  document.getElementById("resultadoBusca").style.display = "block"
}

function editarCliente(id) {
  console.log("Editando cliente ID:", id)
  buscarClienteParaEdicao(id)
}

async function buscarClienteParaEdicao(id) {
  try {
    const response = await apiRequest(`${API_ENDPOINTS.clientes}/${id}`)

    if (response.success && response.data) {
      const cliente = response.data

      document.getElementById("nome").value = cliente.nome
      document.getElementById("documento").value = formatarCPF(cliente.documento)
      document.getElementById("email").value = cliente.email
      document.getElementById("telefone").value = formatarTelefone(cliente.telefone)
      document.getElementById("endereco").value = cliente.endereco

      clienteEditando = id
      document.getElementById("btnSubmit").textContent = "Atualizar Cliente"
      document.getElementById("btnCancelar").style.display = "inline-block"

      document.getElementById("clienteForm").scrollIntoView({ behavior: "smooth" })
    } else {
      handleApiResponse(response)
    }
  } catch (error) {
    console.error("Erro ao buscar cliente para edição:", error)
    showAlert("Erro de conexão com o servidor", "error")
  }
}

async function excluirCliente(id) {
  if (!confirm("Tem certeza que deseja excluir este cliente?")) {
    return
  }

  console.log("Excluindo cliente ID:", id)

  try {
    const response = await apiRequest(`${API_ENDPOINTS.clientes}/${id}`, {
      method: "DELETE",
    })

    if (handleApiResponse(response, "Cliente excluído com sucesso!")) {
      document.getElementById("resultadoBusca").style.display = "none"
      document.getElementById("buscarId").value = ""
    }
  } catch (error) {
    console.error("Erro ao excluir cliente:", error)
    showAlert("Erro de conexão com o servidor", "error")
  }
}

async function listarTodosClientes() {
  console.log("Listando todos os clientes...")

  try {
    const response = await apiRequest(API_ENDPOINTS.clientes)

    if (response.success && response.data) {
      exibirListaClientes(response.data)
    } else {
      handleApiResponse(response)
      document.getElementById("resultadoBusca").style.display = "none"
    }
  } catch (error) {
    console.error("Erro ao listar clientes:", error)
    showAlert("Erro de conexão com o servidor", "error")
    document.getElementById("resultadoBusca").style.display = "none"
  }
}

function exibirListaClientes(clientes) {
  console.log("Exibindo lista de clientes:", clientes)

  if (!clientes || clientes.length === 0) {
    showAlert("Nenhum cliente encontrado", "info")
    document.getElementById("resultadoBusca").style.display = "none"
    return
  }

  const container = document.getElementById("dadosCliente")

  let html = `<div class="clientes-lista">
    <h4>📋 Lista de Clientes (${clientes.length} encontrados)</h4>`

  clientes.forEach((cliente) => {
    html += `
      <div class="cliente-item">
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <div>
            <p><strong>ID:</strong> ${cliente.id} | <strong>Nome:</strong> ${cliente.nome}</p>
            <p><strong>CPF:</strong> ${formatarCPF(cliente.documento)} | <strong>E-mail:</strong> ${cliente.email}</p>
            <p><strong>Telefone:</strong> ${formatarTelefone(cliente.telefone)}</p>
            <p><strong>Endereço:</strong> ${cliente.endereco}</p>
          </div>
          <div class="actions">
            <button class="btn btn-warning btn-sm" onclick="editarCliente(${cliente.id})" style="margin: 2px;">Editar</button>
            <button class="btn btn-danger btn-sm" onclick="excluirCliente(${cliente.id})" style="margin: 2px;">Excluir</button>
          </div>
        </div>
      </div>
    `
  })

  html += "</div>"
  container.innerHTML = html
  document.getElementById("resultadoBusca").style.display = "block"

  document.getElementById("buscarId").value = ""
}

function limparFormulario() {
  document.getElementById("clienteForm").reset()
  clienteEditando = null
  document.getElementById("btnSubmit").textContent = "Cadastrar Cliente"
  document.getElementById("btnCancelar").style.display = "none"
}

// ===== INICIALIZAÇÃO =====
document.addEventListener("DOMContentLoaded", () => {
  console.log("DOM carregado, inicializando eventos...")

  // Formatação automática do CPF
  document.getElementById("documento").addEventListener("input", (e) => {
    let value = e.target.value.replace(/\D/g, "")
    if (value.length <= 11) {
      value = value.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4")
      e.target.value = value
    }
  })

  // Formatação automática do telefone
  document.getElementById("telefone").addEventListener("input", (e) => {
    let value = e.target.value.replace(/\D/g, "")
    if (value.length <= 11) {
      if (value.length === 11) {
        value = value.replace(/(\d{2})(\d{5})(\d{4})/, "($1) $2-$3")
      } else if (value.length === 10) {
        value = value.replace(/(\d{2})(\d{4})(\d{4})/, "($1) $2-$3")
      }
      e.target.value = value
    }
  })

  // Event listeners para botões
  document.getElementById("clienteForm").addEventListener("submit", salvarCliente)
  document.getElementById("btnBuscar").addEventListener("click", buscarCliente)
  document.getElementById("btnListarTodos").addEventListener("click", listarTodosClientes)
  document.getElementById("btnCancelar").addEventListener("click", limparFormulario)

  console.log("Eventos inicializados com sucesso!")
})
